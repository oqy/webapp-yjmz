package com.minyisoft.webapp.yjmz.common.security;

import java.util.List;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.model.ISystemUserObject;
import com.minyisoft.webapp.core.model.PermissionInfo;
import com.minyisoft.webapp.core.security.shiro.AbstractShiroDbRealm;
import com.minyisoft.webapp.core.security.shiro.BasePrincipal;
import com.minyisoft.webapp.yjmz.common.model.RoleInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.service.UserService;

public class ShiroDbRealm extends AbstractShiroDbRealm<UserInfo, RoleInfo> {
	/**
	 * 登录密码哈希算法
	 */
	private static final String hashAlgorithm = "MD5";
	/**
	 * 登录密码哈希迭代次数
	 */
	private static final int hashInterations = 1024;

	@Autowired
	private UserService userService;

	@Override
	protected Object getAuthorizationCacheKey(PrincipalCollection principals) {
		return principals != null && principals.getPrimaryPrincipal() != null ? principals.getPrimaryPrincipal()
				: super.getAuthenticationCacheKey(principals);
	}

	@Override
	public String getHashAlgorithm() {
		return hashAlgorithm;
	}

	@Override
	public int getHashInterations() {
		return hashInterations;
	}

	@Override
	public UserInfo getUserByLoginName(String userLoginName) {
		return userService.getValue(userLoginName);
	}

	@Override
	public List<RoleInfo> getUserRoles(UserInfo user, ISystemOrgObject org) {
		return userService.getUserRoles(user, org);
	}

	@Override
	public List<PermissionInfo> getUserPermissions(UserInfo user, ISystemOrgObject org) {
		return userService.getUserPermissions(user, org);
	}

	@Override
	public ISystemOrgObject getSystemOrg(BasePrincipal basePrincipal) {
		return basePrincipal != null ? basePrincipal.getSystemOrg() : null;
	}

	@Override
	public BasePrincipal createPrincipal(ISystemUserObject user) {
		return new BasePrincipal(user);
	}

	/**
	 * 加密密码
	 * 
	 * @param plainPassword
	 * @param passwordSalt
	 * @return
	 */
	public static String hashPassword(final String plainPassword, final String passwordSalt) {
		return new SimpleHash(hashAlgorithm, plainPassword, passwordSalt, hashInterations).toHex();
	}
}
