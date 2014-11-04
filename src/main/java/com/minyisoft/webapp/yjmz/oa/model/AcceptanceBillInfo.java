package com.minyisoft.webapp.yjmz.oa.model;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.yjmz.common.model.CompanyWorkFlowBillBaseInfo;

@Label("验收单")
@Getter
@Setter
@ModelKey(0x1497AE4915CL)
public class AcceptanceBillInfo extends CompanyWorkFlowBillBaseInfo {

	@Override
	public String getProcessInstanceName() {
		if (getSourceBill() instanceof CompanyWorkFlowBillBaseInfo) {
			return ((CompanyWorkFlowBillBaseInfo) getSourceBill()).getProcessInstanceName() + "验收单";
		}
		return null;
	}

	private static final String PROCESS_VARIABLE_NAME = "acceptanceBill";

	@Override
	public String getBusinessModelProcessVariableName() {
		return PROCESS_VARIABLE_NAME;
	}

}
