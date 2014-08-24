package com.minyisoft.webapp.yjmz.oa.model.criteria;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.CompanyBaseCriteria;

@Getter
@Setter
public class PurchaseReqBillCriteria extends CompanyBaseCriteria {
	// 采购单浏览人
	private UserInfo viewer;
}
