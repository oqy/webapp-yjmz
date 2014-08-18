package com.minyisoft.webapp.yjmz.common.model;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotBlank;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.core.model.DataBaseInfo;
import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.yjmz.common.model.enumField.SupportedWorkFlowTypeEnum;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowStatusEnum;

/**
 * @author qingyong_ou 工作流配置信息
 */
@Getter
@Setter
@ModelKey(0x14487125124L)
public class WorkFlowConfigInfo extends DataBaseInfo {
	@Label("流程ID")
	@NotBlank
	private String processDefinitionId;
	@Label("流程状态")
	@NotNull
	private WorkFlowStatusEnum workFlowStatus;
	@Label("流程定义组织")
	@NotNull
	private ISystemOrgObject defineOrg;
	@Label("流程类型")
	@NotNull
	private SupportedWorkFlowTypeEnum workFlowType;
}
