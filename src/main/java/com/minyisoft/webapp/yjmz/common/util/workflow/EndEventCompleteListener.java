package com.minyisoft.webapp.yjmz.common.util.workflow;

import java.util.Date;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.history.HistoricProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.service.utils.ServiceUtils;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.yjmz.common.model.CompanyWorkFlowBillBaseInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.entity.WeixinTemplateMessage;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowProcessStatusEnum;
import com.minyisoft.webapp.yjmz.common.service.MessageService;
import com.minyisoft.webapp.yjmz.common.service.UserService;

@SuppressWarnings("serial")
@Component
public class EndEventCompleteListener implements ExecutionListener {
	@Value("${web.domain}")
	private String webDomain;
	@Autowired
	private UserService userService;
	@Autowired
	private MessageService messageService;

	public void notify(DelegateExecution execution) throws Exception {
		IModelObject modelObject = ObjectUuidUtils.getObject(execution.getProcessBusinessKey());
		if (modelObject instanceof CompanyWorkFlowBillBaseInfo) {
			// 保存工作流程实例id
			((CompanyWorkFlowBillBaseInfo) modelObject).setProcessStatus(WorkFlowProcessStatusEnum.FINISHED);
			((CompanyWorkFlowBillBaseInfo) modelObject).setProcessEndDate(new Date());
			ServiceUtils.getService(modelObject.getClass()).save(modelObject);

			HistoricProcessInstance historicProcessInstance = ActivitiHelper.PROCESS_ENGINE.getHistoryService()
					.createHistoricProcessInstanceQuery().processInstanceBusinessKey(modelObject.getId())
					.singleResult();
			UserInfo startUser = null;
			if ((startUser = userService.getValue(historicProcessInstance.getStartUserId())) != null
					&& StringUtils.isNotBlank(startUser.getWeixinOpenId())) {
				messageService.sendWeixinTemplateMessage(startUser.getWeixinOpenId(),
						WeixinTemplateMessage.ORDER_STATUS_NOTIFY,
						webDomain + "/viewDetail.html?billId=" + modelObject.getId(),
						"您好，您提交审批的" + ObjectUuidUtils.getClassLabel(modelObject.getClass()) + "已审批完毕\n",
						((CompanyWorkFlowBillBaseInfo) modelObject).getProcessInstanceName(),
						WorkFlowProcessStatusEnum.FINISHED.getDescription(),
						DateFormatUtils.format(new Date(), "yyyy年M月d日HH时mm分"), "\n点击可查看具体审批流程");
			}
		}
	}
}
