package com.minyisoft.webapp.yjmz.oa.model;

import java.util.List;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.yjmz.common.model.CompanyBillBaseInfo;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowProcessStatusEnum;

/**
 * @author qingyong_ou 采购单
 */
@Label("采购单")
@Getter
@Setter
@ModelKey(0x147E349704CL)
public class PurchaseReqBillInfo extends CompanyBillBaseInfo implements WorkFlowBusinessModel {
	@Label("采购分录")
	@Size(min = 1)
	private List<PurchaseReqEntryInfo> entry;
	// 工作流流程实例id
	private String processInstanceId;
	// 工作流程状态
	private WorkFlowProcessStatusEnum processStatus = WorkFlowProcessStatusEnum.UNSTARTED;

	@Override
	public String getProcessInstanceName() {
		StringBuffer sb = new StringBuffer(getCompany().getName());
		if (getDepartment() != null) {
			sb.append(getDepartment().getName());
		}
		return sb.append("采购单").toString();
	}

	@Override
	public boolean isProcessUnStarted() {
		return StringUtils.isBlank(processInstanceId);
	}

	private static final String PROCESS_VARIABLE_NAME = "purchaseReqBill";

	@Override
	public String getBusinessModelProcessVariableName() {
		return PROCESS_VARIABLE_NAME;
	}
}
