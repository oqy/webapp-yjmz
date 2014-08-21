package com.minyisoft.webapp.yjmz.oa.model;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotBlank;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.yjmz.common.model.CompanyBillBaseInfo;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;

/**
 * @author qingyong_ou 工作报告
 */
@Getter
@Setter
@ModelKey(0x147E1C6843FL)
public class ReportInfo extends CompanyBillBaseInfo implements WorkFlowBusinessModel {
	// 档案编号
	private String fileNumber;
	@Label("报告标题")
	@NotBlank
	private String reportTitle;
	// 附件url
	private String attachmentUrl;
	// 工作流流程实例id
	private String processInstanceId;

	@Override
	public String getName() {
		StringBuffer sb = new StringBuffer(getCompany().getName());
		if (getDepartment() != null) {
			sb.append(getDepartment().getName());
		}
		return sb.append("工作报告[").append(getBillNumber()).append("]").toString();
	}
}
