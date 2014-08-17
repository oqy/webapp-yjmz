package com.minyisoft.webapp.yjmz.oa.model;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.core.model.BillBaseInfo;
import com.minyisoft.webapp.yjmz.common.model.DepartmentInfo;

/**
 * @author qingyong_ou 采购单
 */
@Getter
@Setter
@ModelKey(0x147E349704CL)
public class PurchaseReqBillInfo extends BillBaseInfo {
	@Label("所属部门")
	@NotNull
	private DepartmentInfo department;
	@Label("采购分录")
	@Min(1)
	private List<PurchaseReqEntryInfo> entry;
}
