package com.minyisoft.webapp.yjmz.common.service;

import java.util.List;

import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.model.PermissionInfo;
import com.minyisoft.webapp.core.service.BaseService;
import com.minyisoft.webapp.yjmz.common.model.RoleInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.UserCriteria;

public interface UserService extends BaseService<UserInfo, UserCriteria> {
	/**
	 * 用户登录
	 * 
	 * @param userLoginInputString
	 * @param userPassword
	 * @return 成功登录用户
	 */
	UserInfo userLogin(String userLoginInputString, String userPassword);

	/**
	 * 当前登录用户切换登录组织架构
	 * 
	 * @param newOrg
	 */
	void currentUserSwitchOrg(ISystemOrgObject newOrg);

	/**
	 * 编辑指定企业会员账户并赋予角色
	 * 
	 * @param org
	 * @param targetUser
	 * @param upperUser
	 * @param roles
	 */
	void editOrgUser(ISystemOrgObject org, UserInfo targetUser, UserInfo upperUser, RoleInfo... roles);

	/**
	 * 获取指定用户于指定组织架构包含角色
	 * 
	 * @param user
	 * @param org
	 * @return
	 */
	List<RoleInfo> getUserRoles(UserInfo user, ISystemOrgObject org);

	/**
	 * 获取指定用户于指定组织架构包含权限
	 * 
	 * @param user
	 * @param org
	 * @return
	 */
	List<PermissionInfo> getUserPermissions(UserInfo user, ISystemOrgObject org);

	/**
	 * 删除指定组织架构用户
	 * 
	 * @param user
	 * @param org
	 */
	void deleteOrgUser(UserInfo user, ISystemOrgObject org);

	/**
	 * 绑定微信用户
	 * 
	 * @param user
	 * @param weixinOpenId
	 */
	void bindWeixinOpenId(UserInfo user, String weixinOpenId);
}
