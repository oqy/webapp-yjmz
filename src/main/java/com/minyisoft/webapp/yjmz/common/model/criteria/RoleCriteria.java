package com.minyisoft.webapp.yjmz.common.model.criteria;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.model.criteria.BaseCriteria;

@Getter
@Setter
public class RoleCriteria extends BaseCriteria {
	// 所属组织
	private ISystemOrgObject org;
}
