package com.minyisoft.webapp.yjmz.common.model.criteria;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.model.criteria.BaseCriteria;
import com.minyisoft.webapp.yjmz.common.model.DepartmentInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;

@Getter
@Setter
public class UserOrgRelationCriteria extends BaseCriteria {
	// 所属组织
	private ISystemOrgObject org;
	// 部门
	private DepartmentInfo[] departments;
	// 上级用户
	private UserInfo upperUser;

	public void setDepartments(DepartmentInfo... departments) {
		this.departments = departments;
	}
}
