package com.minyisoft.webapp.yjmz.oa.model;

import java.util.List;

import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.yjmz.common.model.CompanyBillBaseInfo;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;

/**
 * @author qingyong_ou 采购单
 */
@Getter
@Setter
@ModelKey(0x147E349704CL)
public class PurchaseReqBillInfo extends CompanyBillBaseInfo implements WorkFlowBusinessModel {
	@Label("采购分录")
	@Min(1)
	private List<PurchaseReqEntryInfo> entry;
	// 工作流流程实例id
	private String processInstanceId;

	@Override
	public String getName() {
		StringBuffer sb = new StringBuffer(getCompany().getName());
		if (getDepartment() != null) {
			sb.append(getDepartment().getName());
		}
		return sb.append("采购单[").append(getBillNumber()).append("]").toString();
	}
}
