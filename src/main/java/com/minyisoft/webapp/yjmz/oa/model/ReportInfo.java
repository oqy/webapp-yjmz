package com.minyisoft.webapp.yjmz.oa.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotBlank;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.yjmz.common.model.AttachmentInfo;
import com.minyisoft.webapp.yjmz.common.model.CompanyWorkFlowBillBaseInfo;
import com.minyisoft.webapp.yjmz.common.model.DepartmentInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;

/**
 * @author qingyong_ou 工作报告
 */
@Label("工作报告")
@Getter
@Setter
@ModelKey(0x147E1C6843FL)
public class ReportInfo extends CompanyWorkFlowBillBaseInfo {
	// 档案编号
	private String fileNumber;
	@Label("报告标题")
	@NotBlank
	private String reportTitle;
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

	private static final String PROCESS_VARIABLE_NAME = "report";

	@Override
	public String getBusinessModelProcessVariableName() {
		return PROCESS_VARIABLE_NAME;
	}
}
