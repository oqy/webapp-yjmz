package com.minyisoft.webapp.yjmz.common.model.criteria;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.model.criteria.BaseCriteria;
import com.minyisoft.webapp.yjmz.common.model.enumField.CompanyStatusEnum;

@Getter
@Setter
public class CompanyCriteria extends BaseCriteria {
	// 公司状态
	private CompanyStatusEnum status;
}
