package com.minyisoft.webapp.yjmz.common.util.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.service.utils.ServiceUtils;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowProcessStatusEnum;

@SuppressWarnings("serial")
public class StartEventCompleteListener implements ExecutionListener {

	public void notify(DelegateExecution execution) throws Exception {
		IModelObject modelObject = ObjectUuidUtils.getObject(execution.getProcessBusinessKey());
		if (modelObject instanceof WorkFlowBusinessModel) {
			// 保存工作流程实例id
			((WorkFlowBusinessModel) modelObject).setProcessInstanceId(execution.getId());
			((WorkFlowBusinessModel) modelObject).setProcessStatus(WorkFlowProcessStatusEnum.RUNNING);
			ServiceUtils.getService(modelObject.getClass()).save(modelObject);
		}
	}
}
