package com.minyisoft.webapp.yjmz.common.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.minyisoft.webapp.core.exception.ServiceException;
import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.model.PermissionInfo;
import com.minyisoft.webapp.core.security.shiro.BasePrincipal;
import com.minyisoft.webapp.core.security.shiro.cache.ShiroClusterCacheManager;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.model.RoleInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.UserCriteria;
import com.minyisoft.webapp.yjmz.common.persistence.PermissionDao;
import com.minyisoft.webapp.yjmz.common.persistence.RoleDao;
import com.minyisoft.webapp.yjmz.common.persistence.UserDao;
import com.minyisoft.webapp.yjmz.common.service.UserService;

@Service("userService")
public class UserServiceImpl extends BaseServiceImpl<UserInfo, UserCriteria, UserDao> implements UserService {
	//@Autowired
	//private SecurityDao securityDao;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private PermissionDao psermissionDao;

	@Override
	public UserInfo userLogin(String userLoginInputString, String userPassword) {
		Subject securitySubject = SecurityUtils.getSubject();
		if (securitySubject != null && securitySubject.isAuthenticated()) {
			securitySubject.logout();
		}

		UserInfo loginUser = getValue(userLoginInputString);
		if (loginUser == null) {
			throw new ServiceException("不存在指定的系统用户信息");
		}

		UsernamePasswordToken token = new UsernamePasswordToken(loginUser.getUserLoginName(), userPassword);
		token.setRememberMe(true);
		securitySubject.login(token);

		// 累增用户登录次数并记录登录日志
		getBaseDao().increaseUserLoginCount(loginUser);
		/*
		 * if (loginDetail == null) { loginDetail = new UserLoginLogEntity(); }
		 * loginDetail.setUser(loginUser);
		 * userDao.insertUserLoginLog(loginDetail);
		 */
		return loginUser;
	}

	@Override
	public void currentUserSwitchOrg(ISystemOrgObject newOrg) {
		UserInfo currentUser = com.minyisoft.webapp.yjmz.common.security.SecurityUtils.getCurrentUser();
		if (currentUser == null) {
			throw new ServiceException("当前登录用户信息已失效，无法进行切换操作");
		} else if (newOrg != null && !currentUser.isBelongTo(newOrg)) {
			throw new ServiceException("当前登录用户不属于指定组织有效用户，不允许切换到指定组织");
		}

		// 设置登录用户关联信息
		Subject securitySubject = SecurityUtils.getSubject();
		BasePrincipal principal = (BasePrincipal) securitySubject.getPrincipal();
		// 去除当前principal授权的缓存信息
		((DefaultSecurityManager) SecurityUtils.getSecurityManager()).getCacheManager()
				.getCache(ShiroClusterCacheManager.SESSION_CLUSTER_AUTHORIZATION_CACHE_NAME)
				.remove(principal.toString());
		if (newOrg != null) {
			principal.setSystemOrg(newOrg);
		} else {
			principal.setSystemOrg(null);
		}
		// 将当前登录用户信息同步到session中
		((DefaultSecurityManager) SecurityUtils.getSecurityManager()).getSubjectDAO().save(securitySubject);
	}
	
	@Override
	public void delete(UserInfo info) {
		throw new ServiceException("不允许删除系统用户信息");
	}
	
	@Override
	public void editOrgUser(ISystemOrgObject org, UserInfo targetUser, UserInfo upperUser, RoleInfo... roles) {
		/*Assert.notNull(org, "待编辑用户所在组织架构不能为空");
		Assert.notNull(targetUser, "待编辑用户不能为空");
		Assert.isTrue(upperUser == null || upperUser.isBelongTo(org), "上级用户不属于指定组织");
		Assert.isTrue(upperUser == null || !upperUser.equals(targetUser), "目标用户与上级用户相同");

		if (org instanceof CompanyInfo && upperUser != null && ArrayUtils.isEmpty(roles)) {
			// 授予上级用户的所有角色
			List<RoleInfo> roleList = getUserRoles(upperUser, org);
			if (!CollectionUtils.isEmpty(roleList)) {
				roles = roleList.toArray(new RoleInfo[roleList.size()]);
			}
		}
		submit(targetUser);
		// 删除用户所有角色
		securityDao.deleteUserOrgRoles(targetUser, org);

		// 设置角色
		if (ArrayUtils.isNotEmpty(roles)) {
			for (RoleInfo role : roles) {
				securityDao.insertUserRole(targetUser, org, role);
			}
		}

		// 删除已有对应组织信息
		getBaseDao().deleteUserOrganization(targetUser, org);
		// 增加对应组织信息
		getBaseDao().insertUserOrganization(
				targetUser,
				null,
				org,
				upperUser,
				upperUser != null ? upperUser.getUserPath(org) + SystemConstant.ID_SEPARATOR + targetUser.getId()
						: SystemConstant.ID_SEPARATOR + targetUser.getId());*/
	}

	@Override
	protected void _validateDataBeforeSubmit(UserInfo info) {
		if (info.getDefaultLoginOrg() != null) {
			Assert.isTrue(info.isBelongTo(info.getDefaultLoginOrg()), "待编辑用户并不隶属于待设置的默认登录组织");
		}
	}
	
	@Override
	protected void _validateDataBeforeAdd(UserInfo info) {
		if (StringUtils.isBlank(info.getUserLoginName())) {
			info.setUserLoginName(info.getCellPhoneNumber());
		}
		if (StringUtils.isBlank(info.getUserPassword())) {
			info.constructUserPassword(info.getCellPhoneNumber());
		}
	}

	@Override
	public List<RoleInfo> getUserRoles(UserInfo user, ISystemOrgObject org) {
		List<RoleInfo> roles = roleDao.getUserRole(user, org);
		return ImmutableList.copyOf(roles);
	}

	@Override
	public List<PermissionInfo> getUserPermissions(UserInfo user, ISystemOrgObject org) {
		List<PermissionInfo> permissionList = Lists.newArrayList();
		List<RoleInfo> roleList = getUserRoles(user, org);
		for (RoleInfo role : roleList) {
			permissionList.addAll(psermissionDao.getRolePermission(role));
		}
		return ImmutableList.copyOf(permissionList);
	}

	@Override
	public void deleteOrgUser(UserInfo user, ISystemOrgObject org) {
		/*if (user == null || org == null || !user.isBelongTo(org)) {
			return;
		}
		// 若待删除组织为用户默认登录组织，清空设定
		if (org.equals(user.getDefaultLoginOrg())) {
			user.setDefaultLoginOrg(null);
			save(user);
		}
		// 删除用户所在组织所有角色
		securityDao.deleteUserOrgRoles(user, org);
		// 删除用户组织信息
		getBaseDao().deleteUserOrganization(user, org);
		// 设定新的上下级关系
		getBaseDao().updateUserPathAfterUserOrgDelete(user, user.getUpperUser(org), org);*/
	}

	@Override
	public void bindWeixinOpenId(UserInfo user, String weixinOpenId) {
		Assert.notNull(user, "待绑定账户不能为空");
		Assert.hasText(weixinOpenId, "待绑定微信openId不能为空");
		getBaseDao().clearWeixinOpenId(weixinOpenId);
		user.setWeixinOpenId(weixinOpenId);
		save(user);
	}
}
