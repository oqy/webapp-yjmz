package com.minyisoft.webapp.yjmz.common.util.workflow;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.IdentityLink;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.service.utils.ServiceUtils;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;
import com.minyisoft.webapp.yjmz.common.model.entity.WeixinTemplateMessage;
import com.minyisoft.webapp.yjmz.common.service.MessageService;
import com.minyisoft.webapp.yjmz.common.service.UserService;

@SuppressWarnings("serial")
@Component
public class UserTaskCreateListener implements TaskListener {
	@Autowired
	private UserService userService;
	@Autowired
	private MessageService messageService;
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
		String[] weixinTemplateMessages = new String[5];
		weixinTemplateMessages[0] = "您好，您有一条新的待处理工作流任务\n";
		weixinTemplateMessages[1] = ((WorkFlowBusinessModel) model).getProcessInstanceName();
		weixinTemplateMessages[2] = delegateTask.getName();
		weixinTemplateMessages[3] = DateFormatUtils.format(delegateTask.getCreateTime(), "yyyy年M月d日HH时mm分");
		weixinTemplateMessages[4] = "\n请您及时处理";

		// 若任务存在指定执行者，向执行者发送通知消息
		if (delegateTask.getAssignee() != null) {
			_sendWeixinNotifyMessage(userService.getValue(delegateTask.getAssignee()), (WorkFlowBusinessModel) model,
					weixinTemplateMessages);
		}
		// 若任务存在候选执行者，向候选执行者发送通知消息
		else if (!delegateTask.getCandidates().isEmpty()) {
			for (IdentityLink link : delegateTask.getCandidates()) {
				_sendWeixinNotifyMessage(userService.getValue(link.getUserId()), (WorkFlowBusinessModel) model,
						weixinTemplateMessages);
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
	private void _sendWeixinNotifyMessage(UserInfo user, WorkFlowBusinessModel model, String[] weixinTemplateMessages) {
		if (user != null && StringUtils.isNotBlank(user.getWeixinOpenId())) {
			messageService.sendWeixinTemplateMessage(user.getWeixinOpenId(), WeixinTemplateMessage.ORDER_STATUS_NOTIFY,
					webDomain + "/viewDetail.html?billId=" + model.getId(), weixinTemplateMessages);
		}
	}
}
