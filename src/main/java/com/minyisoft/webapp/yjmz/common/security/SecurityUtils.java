package com.minyisoft.webapp.yjmz.common.security;

import org.apache.shiro.subject.Subject;

import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.security.shiro.BasePrincipal;
import com.minyisoft.webapp.core.security.utils.PermissionUtils;
import com.minyisoft.webapp.yjmz.common.model.CompanyInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;

/**
 * @author qingyong_ou 用户登录、安全检查工具类
 */
public final class SecurityUtils {
	private SecurityUtils() {

	}

	/**
	 * 获取系统当前登录信息
	 * 
	 * @return
	 */
	public static BasePrincipal getSystemPrincipal() {
		try {
			Subject subject = org.apache.shiro.SecurityUtils.getSubject();
			if (subject != null && subject.isAuthenticated()) {
				return (BasePrincipal) subject.getPrincipal();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * 获取当前客户管理平台登录用户所在公司，只包含公司id信息
	 * 
	 * @return
	 */
	public static CompanyInfo getCurrentCompany() {
		ISystemOrgObject currentCompany = getCurrentOrg();
		return currentCompany instanceof CompanyInfo ? (CompanyInfo) currentCompany : null;
	}

	/**
	 * 获取当前登录用户信息，没有登录则返回null
	 * 
	 * @return
	 */
	public static UserInfo getCurrentUser() {
		BasePrincipal principal = getSystemPrincipal();
		return principal == null ? null : (UserInfo) principal.getSystemUser();
	}

	/**
	 * 获取当前登录用户所在组织架构，没有登录则返回null
	 * 
	 * @return
	 */
	public static ISystemOrgObject getCurrentOrg() {
		BasePrincipal principal = getSystemPrincipal();
		return principal == null ? null : principal.getSystemOrg();
	}

	/**
	 * 判断当前用户是否拥有后台管理平台系统管理员角色
	 * 
	 * @return
	 */
	public static boolean isCurrentUserAdministrator() {
		return PermissionUtils.hasRole(PermissionUtils.ADMINISTRATOR_ROLE);
	}

	public static void checkIsCurrentUserAdministrator() {
		if (!isCurrentUserAdministrator()) {
			throw new SecurityException("当前用户不具备后台管理平台系统管理员权限，无法进行当前操作");
		}
	}
}
