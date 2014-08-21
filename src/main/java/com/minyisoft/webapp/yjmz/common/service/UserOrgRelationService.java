package com.minyisoft.webapp.yjmz.common.service;

import com.minyisoft.webapp.core.service.BaseService;
import com.minyisoft.webapp.yjmz.common.model.RoleInfo;
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
}
