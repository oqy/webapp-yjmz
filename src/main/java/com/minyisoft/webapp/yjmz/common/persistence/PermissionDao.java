package com.minyisoft.webapp.yjmz.common.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.model.PermissionInfo;
import com.minyisoft.webapp.core.persistence.BaseDao;
import com.minyisoft.webapp.yjmz.common.model.RoleInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.PermissionCriteria;

public interface PermissionDao extends BaseDao<PermissionInfo, PermissionCriteria> {
	/**
	 * 获取指定角色拥有的权限
	 * 
	 * @param role
	 * @return
	 */
	List<PermissionInfo> getRolePermission(RoleInfo role);

	/**
	 * 删除所有权限
	 * 
	 * @return
	 */
	int flushPermission();

	/**
	 * 删除无效的角色权限
	 * 
	 * @return
	 */
	int deleteUselessRolePermission();

	/**
	 * 判断指定用户在指定组织是否拥有指定权限
	 * 
	 * @param user
	 * @param org
	 * @param permissionString
	 * @return
	 */
	boolean hasPermission(@Param("user") UserInfo user, @Param("org") ISystemOrgObject org,
			@Param("value") String permissionString);
}
