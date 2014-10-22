package com.minyisoft.webapp.yjmz.common.util.workflow;

import java.util.Map;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.IdentityLink;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.service.utils.ServiceUtils;
import com.minyisoft.webapp.weixin.common.model.dto.send.TemplateMessageData;
import com.minyisoft.webapp.weixin.common.service.WeixinCommonService;
import com.minyisoft.webapp.weixin.common.service.WeixinPostService;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;
import com.minyisoft.webapp.yjmz.common.service.UserService;
import com.minyisoft.webapp.yjmz.weixin.web.interceptor.WeixinOAuthInterceptor;

@SuppressWarnings("serial")
@Component
public class UserTaskCreateListener implements TaskListener {
	@Autowired
	private UserService userService;
	@Autowired
	private WeixinCommonService weixinCommonService;
	@Autowired
	private WeixinPostService weixinPostService;
	@Value("${weixin.notify_template_id}")
	private String weixinNotifyTemplateId;
	@Value("${web.domain}")
	private String webDomain;

	@Override
	public void notify(DelegateTask delegateTask) {
		TaskEntity task = (TaskEntity) delegateTask;
		IModelObject model = ServiceUtils.getModel(task.getExecution().getProcessBusinessKey());
		if (!(model instanceof WorkFlowBusinessModel)) {
			return;
		}

		// 微信模板消息待发送内容
		Map<String, TemplateMessageData> data = Maps.newHashMap();
		data.put("first", new TemplateMessageData("您好，您有一条新的待处理工作流任务\n"));
		data.put("keyword1", new TemplateMessageData(((WorkFlowBusinessModel) model).getProcessInstanceName()));
		data.put("keyword2", new TemplateMessageData(delegateTask.getName()));
		data.put("keyword3",
				new TemplateMessageData(DateFormatUtils.format(delegateTask.getCreateTime(), "yyyy年M月d日HH时mm分")));
		data.put("remark", new TemplateMessageData("\n请您及时处理"));

		// 若任务存在指定执行者，向执行者发送通知消息
		if (delegateTask.getAssignee() != null) {
			_sendWeixinNotifyMessage(userService.getValue(delegateTask.getAssignee()), (WorkFlowBusinessModel) model,
					data);
		}
		// 若任务存在候选执行者，向候选执行者发送通知消息
		else if (!delegateTask.getCandidates().isEmpty()) {
			for (IdentityLink link : delegateTask.getCandidates()) {
				_sendWeixinNotifyMessage(userService.getValue(link.getUserId()), (WorkFlowBusinessModel) model, data);
			}
		}
	}

	/**
	 * 发送微信通知消息
	 * 
	 * @param user
	 * @param model
	 * @param messageData
	 */
	private void _sendWeixinNotifyMessage(UserInfo user, WorkFlowBusinessModel model,
			Map<String, TemplateMessageData> messageData) {
		if (user != null && StringUtils.isNotBlank(user.getWeixinOpenId())) {
			weixinPostService.postTemplateMessage(user.getWeixinOpenId(), weixinNotifyTemplateId,
					WeixinOAuthInterceptor.appendWeixinTicket(webDomain + "/viewDetail.html?billId=" + model.getId(),
							weixinCommonService.genWeixinTicket(user.getWeixinOpenId())), messageData);
		}
	}
}
