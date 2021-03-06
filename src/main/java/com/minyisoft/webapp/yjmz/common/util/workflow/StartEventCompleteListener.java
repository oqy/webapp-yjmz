package com.minyisoft.webapp.yjmz.common.util.workflow;

import java.util.Date;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.service.utils.ServiceUtils;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.yjmz.common.model.CompanyWorkFlowBillBaseInfo;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowProcessStatusEnum;

@SuppressWarnings("serial")
public class StartEventCompleteListener implements ExecutionListener {

	public void notify(DelegateExecution execution) throws Exception {
		IModelObject modelObject = ObjectUuidUtils.getObject(execution.getProcessBusinessKey());
		if (modelObject instanceof CompanyWorkFlowBillBaseInfo) {
			// 保存工作流程实例id
			((CompanyWorkFlowBillBaseInfo) modelObject).setProcessInstanceId(execution.getId());
			((CompanyWorkFlowBillBaseInfo) modelObject).setProcessStatus(WorkFlowProcessStatusEnum.RUNNING);
			((CompanyWorkFlowBillBaseInfo) modelObject).setProcessBeginDate(new Date());
			ServiceUtils.getService(modelObject.getClass()).save(modelObject);
		}
	}
}
