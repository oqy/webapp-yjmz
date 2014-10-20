package com.minyisoft.webapp.yjmz.oa.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.yjmz.common.model.AttachmentInfo;
import com.minyisoft.webapp.yjmz.common.model.CompanyBillBaseInfo;
import com.minyisoft.webapp.yjmz.common.model.DepartmentInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowProcessStatusEnum;

/**
 * @author qingyong_ou 工作报告
 */
@Label("工作报告")
@Getter
@Setter
@ModelKey(0x147E1C6843FL)
public class ReportInfo extends CompanyBillBaseInfo implements WorkFlowBusinessModel {
	// 档案编号
	private String fileNumber;
	@Label("报告标题")
	@NotBlank
	private String reportTitle;
	// 工作流流程实例id
	private String processInstanceId;
	// 工作流程状态
	private WorkFlowProcessStatusEnum processStatus = WorkFlowProcessStatusEnum.UNSTARTED;
	// 附件
	private List<AttachmentInfo> attachments;
	// 前置审批部门
	private DepartmentInfo preApproveDepartment;
	// 前置审批部门负责人
	private UserInfo preApproveDepartmentLeader;

	@Override
	public String getProcessInstanceName() {
		StringBuffer sb = new StringBuffer(getCompany().getName());
		if (getDepartment() != null) {
			sb.append(getDepartment().getName());
		}
		return sb.append("工作报告[").append(getBillNumber()).append("]").toString();
	}

	@Override
	public boolean isProcessUnStarted() {
		return StringUtils.isBlank(processInstanceId);
	}

	private static final String PROCESS_VARIABLE_NAME = "report";

	@Override
	public String getBusinessModelProcessVariableName() {
		return PROCESS_VARIABLE_NAME;
	}
}
