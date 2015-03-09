package com.minyisoft.webapp.yjmz.common.service.impl;

import java.text.MessageFormat;
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
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.weixin.mp.util.MpConstant;
import com.minyisoft.webapp.yjmz.common.model.RoleInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.UserCriteria;
import com.minyisoft.webapp.yjmz.common.model.enumField.UserStatusEnum;
import com.minyisoft.webapp.yjmz.common.persistence.RoleDao;
import com.minyisoft.webapp.yjmz.common.persistence.UserDao;
import com.minyisoft.webapp.yjmz.common.security.ShiroDbRealm;
import com.minyisoft.webapp.yjmz.common.service.UserService;

@Service("userService")
public class UserServiceImpl extends BaseServiceImpl<UserInfo, UserCriteria, UserDao> implements UserService {
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private ShiroDbRealm shiroDbRealm;

	@Override
	public UserInfo userLogin(String userLoginInputString, String userPassword) {
		Subject securitySubject = SecurityUtils.getSubject();
		if (securitySubject != null && securitySubject.isAuthenticated()) {
			securitySubject.logout();
		}

		UserInfo loginUser = getValue(userLoginInputString);
		if (loginUser == null || loginUser.getStatus() == UserStatusEnum.DIMISSION) {
			throw new ServiceException(MessageFormat.format("不存在登录账号为[{0}]的系统用户信息", userLoginInputString));
		}

		UsernamePasswordToken token = new UsernamePasswordToken(loginUser.getUserLoginName(), userPassword);
		// token.setRememberMe(true);
		securitySubject.login(token);

		// 累增用户登录次数并记录登录日志
		getBaseDao().increaseUserLoginCount(loginUser);

		// 绑定服务微信服务号openId
		String weixinOpenId = (String) securitySubject.getSession().getAttribute(MpConstant.WEIXIN_OPEN_ID_VAR_NAME);
		if (StringUtils.isNotBlank(weixinOpenId)) {
			getBaseDao().clearWeixinOpenId(weixinOpenId);
			getBaseDao().bindWeixinOpenId(loginUser, weixinOpenId);
			securitySubject.getSession().removeAttribute(MpConstant.WEIXIN_OPEN_ID_VAR_NAME);
		}
		return loginUser;
	}

	@Override
	public void switchOrg(UserInfo currentUser, ISystemOrgObject newOrg) {
		if (currentUser == null) {
			throw new ServiceException("当前登录用户信息已失效，无法进行切换操作");
		} else if (newOrg != null && !currentUser.isBelongTo(newOrg)) {
			throw new ServiceException("当前登录用户不属于指定组织有效用户，不允许切换到指定组织");
		}

		// 设置登录用户关联信息
		Subject securitySubject = SecurityUtils.getSubject();
		BasePrincipal principal = (BasePrincipal) securitySubject.getPrincipal();
		// 去除当前principal授权的缓存信息
		shiroDbRealm.getAuthorizationCache().remove(principal);

		principal.setSystemOrg(newOrg != null ? newOrg : null);
		// 将当前登录用户信息同步到session中
		((DefaultSecurityManager) SecurityUtils.getSecurityManager()).getSubjectDAO().save(securitySubject);
	}

	@Override
	public void delete(UserInfo info) {
		throw new ServiceException("不允许删除系统用户信息");
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
		return ImmutableList.copyOf(roleDao.getUserRoles(user, org));
	}

	@Override
	public List<PermissionInfo> getUserPermissions(UserInfo user, ISystemOrgObject org) {
		List<PermissionInfo> permissionList = Lists.newArrayList();
		for (RoleInfo role : getUserRoles(user, org)) {
			permissionList.addAll(role.getPermissions());
		}
		return ImmutableList.copyOf(permissionList);
	}

	@Override
	protected boolean useModelCache() {
		return true;
	}
}
