package com.minyisoft.webapp.yjmz.common.model.criteria;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.model.criteria.BaseCriteria;
import com.minyisoft.webapp.yjmz.common.model.CompanyInfo;
import com.minyisoft.webapp.yjmz.common.model.DepartmentInfo;

@Getter
@Setter
public abstract class CompanyBaseCriteria extends BaseCriteria {
	// 所属公司
	private CompanyInfo company;
	// 所属部门
	private DepartmentInfo department;
}
