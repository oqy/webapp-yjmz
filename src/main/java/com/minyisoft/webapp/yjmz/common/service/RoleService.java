package com.minyisoft.webapp.yjmz.common.service;

import java.util.List;

import com.minyisoft.webapp.core.model.PermissionInfo;
import com.minyisoft.webapp.core.service.BaseService;
import com.minyisoft.webapp.yjmz.common.model.RoleInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.RoleCriteria;

public interface RoleService extends BaseService<RoleInfo, RoleCriteria> {
	/**
	 * 保存角色信息及设定对应的权限
	 * 
	 * @param role
	 * @param permissions
	 */
	void submitRoleWithPermission(RoleInfo role, List<PermissionInfo> permissions);

	/**
	 * 获取指定角色包含的权限信息
	 * 
	 * @param role
	 */
	List<PermissionInfo> getRolePermissions(RoleInfo role);
}
