package com.minyisoft.webapp.yjmz.weixin.security;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;

import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.security.ShiroDbRealm;

public class ShiroWeixinRealm extends ShiroDbRealm {
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		UserInfo user = getUserByLoginName(token.getUsername());
		if (user != null && StringUtils.isNotBlank(user.getWeixinOpenId())) {
			return new SimpleAuthenticationInfo(createPrincipal(user), user.getWeixinOpenId(), null, getName());
		} else {
			return null;
		}
	}

	@Override
	public boolean isCachingEnabled() {
		return false;
	}

	@Override
	protected void initCredentialsMatcher() {
	}

}
