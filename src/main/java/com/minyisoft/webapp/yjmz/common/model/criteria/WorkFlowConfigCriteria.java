package com.minyisoft.webapp.yjmz.common.model.criteria;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.model.criteria.BaseCriteria;
import com.minyisoft.webapp.yjmz.common.model.enumField.SupportedWorkFlowTypeEnum;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowStatusEnum;

@Getter
@Setter
public class WorkFlowConfigCriteria extends BaseCriteria {
	@Label("流程状态")
	private WorkFlowStatusEnum workFlowStatus = WorkFlowStatusEnum.NORMAL;
	// 流程定义组织
	private ISystemOrgObject defineOrg;
	// 流程类型
	private SupportedWorkFlowTypeEnum workFlowType;
}
