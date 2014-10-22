package com.minyisoft.webapp.yjmz.common.util.workflow;

import java.util.Date;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.history.HistoricProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.service.utils.ServiceUtils;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.weixin.common.model.dto.send.TemplateMessageData;
import com.minyisoft.webapp.weixin.common.service.WeixinCommonService;
import com.minyisoft.webapp.weixin.common.service.WeixinPostService;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowProcessStatusEnum;
import com.minyisoft.webapp.yjmz.common.service.UserService;
import com.minyisoft.webapp.yjmz.weixin.web.interceptor.WeixinOAuthInterceptor;

@SuppressWarnings("serial")
@Component
public class EndEventCompleteListener implements ExecutionListener {
	@Autowired
	private WeixinCommonService weixinCommonService;
	@Autowired
	private WeixinPostService weixinPostService;
	@Value("${weixin.notify_template_id}")
	private String weixinNotifyTemplateId;
	@Value("${web.domain}")
	private String webDomain;
	@Autowired
	private UserService userService;

	public void notify(DelegateExecution execution) throws Exception {
		IModelObject modelObject = ObjectUuidUtils.getObject(execution.getProcessBusinessKey());
		if (modelObject instanceof WorkFlowBusinessModel) {
			// 保存工作流程实例id
			((WorkFlowBusinessModel) modelObject).setProcessStatus(WorkFlowProcessStatusEnum.FINISHED);
			ServiceUtils.getService(modelObject.getClass()).save(modelObject);

			HistoricProcessInstance historicProcessInstance = ActivitiHelper.PROCESS_ENGINE.getHistoryService()
					.createHistoricProcessInstanceQuery().processInstanceBusinessKey(modelObject.getId())
					.singleResult();
			UserInfo startUser = null;
			if ((startUser = userService.getValue(historicProcessInstance.getStartUserId())) != null
					&& StringUtils.isNotBlank(startUser.getWeixinOpenId())) {
				// 微信模板消息待发送内容
				Map<String, TemplateMessageData> messageData = Maps.newHashMap();
				messageData.put("first",
						new TemplateMessageData("您好，您提交审批的" + ObjectUuidUtils.getClassLabel(modelObject.getClass())
								+ "已审批完毕\n"));
				messageData.put("keyword1",
						new TemplateMessageData(((WorkFlowBusinessModel) modelObject).getProcessInstanceName()));
				messageData.put("keyword2",
						new TemplateMessageData(WorkFlowProcessStatusEnum.FINISHED.getDescription()));
				messageData.put("keyword3",
						new TemplateMessageData(DateFormatUtils.format(new Date(), "yyyy年M月d日HH时mm分")));
				messageData.put("remark", new TemplateMessageData("\n点击可查看具体审批流程"));

				weixinPostService.postTemplateMessage(
						startUser.getWeixinOpenId(),
						weixinNotifyTemplateId,
						WeixinOAuthInterceptor.appendWeixinTicket(
								webDomain + "/viewDetail.html?billId=" + modelObject.getId(),
								weixinCommonService.genWeixinTicket(startUser.getWeixinOpenId())), messageData);
			}
		}
	}
}
