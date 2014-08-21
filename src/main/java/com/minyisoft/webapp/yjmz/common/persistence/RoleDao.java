package com.minyisoft.webapp.yjmz.common.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.model.PermissionInfo;
import com.minyisoft.webapp.core.persistence.BaseDao;
import com.minyisoft.webapp.yjmz.common.model.RoleInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.RoleCriteria;

public interface RoleDao extends BaseDao<RoleInfo, RoleCriteria> {
	/**
	 * 获取指定用户拥有的角色
	 * 
	 * @param user
	 * @param org
	 * @return
	 */
	List<RoleInfo> getUserRoles(@Param("user") UserInfo user, @Param("org") ISystemOrgObject org);

	/**
	 * 删除指定用户在指定组织的所有角色
	 * 
	 * @param user
	 * @param org
	 * @return
	 */
	int deleteUserRoles(@Param("user") UserInfo user, @Param("org") ISystemOrgObject org);

	/**
	 * 插入用户与角色对应关系
	 * 
	 * @param user
	 * @param role
	 * @return
	 */
	int insertUserRole(@Param("user") UserInfo user, @Param("role") RoleInfo role);

	/**
	 * 删除指定权限角色的角色——用户对应关系
	 * 
	 * @param role
	 * @return
	 */
	int deleteRoleUser(RoleInfo role);

	/**
	 * 插入角色与权限对应关系
	 * 
	 * @param role
	 * @param permission
	 * @return
	 */
	int insertRolePermission(@Param("role") RoleInfo role, @Param("permission") PermissionInfo permission);

	/**
	 * 删除指定角色的角色——权限对应关系
	 * 
	 * @param role
	 * @return
	 */
	int deleteRolePermission(RoleInfo role);
}
