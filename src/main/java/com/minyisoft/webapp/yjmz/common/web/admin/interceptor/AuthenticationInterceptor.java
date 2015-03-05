package com.minyisoft.webapp.yjmz.common.web.admin.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.minyisoft.webapp.yjmz.common.security.SecurityUtils;

/**
 * @author qingyong_ou 系统管理员身份认证拦截器
 */
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (!SecurityUtils.isCurrentUserAdministrator()) {
			response.sendRedirect(request.getContextPath() + "/manage/index.html");
			return false;
		}
		return true;
	}
}
