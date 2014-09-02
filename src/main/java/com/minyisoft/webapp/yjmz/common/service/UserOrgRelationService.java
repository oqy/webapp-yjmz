package com.minyisoft.webapp.yjmz.common.service;

import java.util.List;

import com.google.common.base.Optional;
import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.service.BaseService;
import com.minyisoft.webapp.yjmz.common.model.DepartmentInfo;
import com.minyisoft.webapp.yjmz.common.model.RoleInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.UserOrgRelationInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.UserOrgRelationCriteria;

public interface UserOrgRelationService extends BaseService<UserOrgRelationInfo, UserOrgRelationCriteria> {
	/**
	 * 保存用户组织关系，并保存用户组织角色
	 * 
	 * @param userOrgRelation
	 * @param roles
	 */
	void submit(UserOrgRelationInfo userOrgRelation, RoleInfo... roles);

	/**
	 * 获取指定组织指定用户的所有下属用户
	 * 
	 * @param org
	 * @param upperUser
	 * @return
	 */
	List<UserInfo> getSubordinateness(ISystemOrgObject org, UserInfo upperUser);

	/**
	 * 获取部门领导
	 * 
	 * @param department
	 * @return
	 */
	Optional<UserInfo> getDepartmentLeader(DepartmentInfo department);
}
