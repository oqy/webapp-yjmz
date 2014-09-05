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
	 * 用户切换登录组织
	 * 
	 * @param currentUser
	 * @param newOrg
	 */
	void switchOrg(UserInfo currentUser, ISystemOrgObject newOrg);

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
	 * 绑定微信用户
	 * 
	 * @param user
	 * @param weixinOpenId
	 */
	void bindWeixinOpenId(UserInfo user, String weixinOpenId);
}
