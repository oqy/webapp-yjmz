package com.minyisoft.webapp.yjmz.common.model.criteria;

import lombok.Getter;
import lombok.Setter;

import org.springframework.util.ClassUtils;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.model.criteria.BaseCriteria;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowStatusEnum;

@Getter
@Setter
public class WorkFlowConfigCriteria extends BaseCriteria {
	@Label("流程状态")
	private WorkFlowStatusEnum workFlowStatus = WorkFlowStatusEnum.NORMAL;
	// 流程定义组织
	private ISystemOrgObject defineOrg;
	// 流程类型
	private Class<? extends WorkFlowBusinessModel> workFlowType;

	@SuppressWarnings("unchecked")
	public void setWorkFlowType(Class<? extends WorkFlowBusinessModel> clazz) {
		workFlowType = (Class<? extends WorkFlowBusinessModel>) ClassUtils.getUserClass(clazz);
	}
}
