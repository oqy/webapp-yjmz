package com.minyisoft.webapp.yjmz.oa.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.core.model.IBillObject;
import com.minyisoft.webapp.yjmz.common.model.AttachmentInfo;
import com.minyisoft.webapp.yjmz.common.model.CompanyWorkFlowBillBaseInfo;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowProcessStatusEnum;

@Label("验收单")
@Getter
@Setter
@ModelKey(0x1497AE4915CL)
public class AcceptanceBillInfo extends CompanyWorkFlowBillBaseInfo {
	// 附件
	private List<AttachmentInfo> attachments;

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

	@Override
	public boolean shouldNotifyObservers(NotifyAction action, IBillObject observer) {
		if (observer instanceof CompanyWorkFlowBillBaseInfo && action == NotifyAction.SAVE
				&& getProcessStatus() == WorkFlowProcessStatusEnum.FINISHED) {
			return true;
		}
		return super.shouldNotifyObservers(action, observer);
	}
}
