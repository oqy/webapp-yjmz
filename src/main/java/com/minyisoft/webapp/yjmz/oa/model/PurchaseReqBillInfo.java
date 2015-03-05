package com.minyisoft.webapp.yjmz.oa.model;

import java.util.List;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.yjmz.common.model.AttachmentInfo;
import com.minyisoft.webapp.yjmz.common.model.CompanyWorkFlowBillBaseInfo;
import com.minyisoft.webapp.yjmz.oa.model.entity.PurchaseProcessReportEntity;

/**
 * @author qingyong_ou 采购单
 */
@Label("采购单")
@Getter
@Setter
@ModelKey(0x147E349704CL)
public class PurchaseReqBillInfo extends CompanyWorkFlowBillBaseInfo {
	@Label("采购分录")
	@Size(min = 1)
	private List<PurchaseReqEntryInfo> entry;
	// 附件
	private List<AttachmentInfo> attachments;
	// 采购进度报告
	private List<PurchaseProcessReportEntity> processReports;

	@Override
	public String getProcessInstanceName() {
		StringBuffer sb = new StringBuffer(getCompany().getName());
		if (getDepartment() != null) {
			sb.append(getDepartment().getName());
		}
		return sb.append("采购单").toString();
	}

	private static final String PROCESS_VARIABLE_NAME = "purchaseReqBill";

	@Override
	public String getBusinessModelProcessVariableName() {
		return PROCESS_VARIABLE_NAME;
	}
}
