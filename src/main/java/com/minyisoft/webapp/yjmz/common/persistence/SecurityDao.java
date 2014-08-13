package com.minyisoft.webapp.yjmz.common.persistence;

import org.apache.ibatis.annotations.Param;

import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.yjmz.common.model.RoleInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;

public interface SecurityDao {
	/**
	 * 为指定组织架构所有用户增加指定角色
	 * 
	 * @param org
	 * @param role
	 * @return
	 */
	int insertOrgUserRole(@Param("org") ISystemOrgObject org, @Param("role") RoleInfo role);

	/**
	 * 删除指定组织架构所有用户的所有角色
	 * 
	 * @param org
	 * @return
	 */
	int deleteOrgUserAllRoles(@Param("org") ISystemOrgObject org);

	/**
	 * 删除指定组织架构所有用户的指定角色
	 * 
	 * @param company
	 * @param roles
	 * @return
	 */
	int deleteOrgUserRoles(@Param("org") ISystemOrgObject org, @Param("roles") RoleInfo... roles);

	/**
	 * 删除指定用户的所有角色
	 * 
	 * @param user
	 * @return
	 */
	int deleteUserRoles(UserInfo user);

	/**
	 * 删除指定用户在指定组织的所有角色
	 * 
	 * @param user
	 * @param org
	 * @return
	 */
	int deleteUserOrgRoles(@Param("user") UserInfo user, @Param("org") ISystemOrgObject org);

	/**
	 * 为指定用户增加指定角色
	 * 
	 * @param user
	 * @param org
	 * @param role
	 * @return
	 */
	int insertUserRole(@Param("user") UserInfo user, @Param("org") ISystemOrgObject org, @Param("role") RoleInfo role);
}
