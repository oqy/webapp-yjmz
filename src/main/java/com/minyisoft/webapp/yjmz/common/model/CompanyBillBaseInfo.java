package com.minyisoft.webapp.yjmz.common.model;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.model.BillBaseInfo;

@Getter
@Setter
public abstract class CompanyBillBaseInfo extends BillBaseInfo {
	@Label("所属公司")
	@NotNull
	private CompanyInfo company;
	// 申请部门
	private DepartmentInfo department;
}
