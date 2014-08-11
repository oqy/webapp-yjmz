package com.minyisoft.webapp.yjmz.weixin.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.base.Optional;
import com.minyisoft.webapp.yjmz.weixin.service.WeixinConnectService;

/**
 * @author qingyong_ou 微信网页授权获取用户基本信息拦截器
 */
public class WeixinOAuthInterceptor extends HandlerInterceptorAdapter {
	private Logger logger = LoggerFactory.getLogger(getClass());
	public final static String WEIXIN_OPEN_ID = "weixinOpenId";
	@Autowired
	private WeixinConnectService weixinConnectService;
	//@Autowired
	//private IUser userService;
	//@Autowired
	//private IUserLogin userLoginService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String weixinOpenId = request.getParameter(WEIXIN_OPEN_ID);
		if (StringUtils.isNotBlank(weixinOpenId)) {
			logger.info(new StringBuffer("由微信客户端进入网页，微信openId：").append(weixinOpenId).append("，待访问网址：")
					.append(request.getRequestURI()).toString());
			request.getSession().setAttribute(WEIXIN_OPEN_ID, weixinOpenId);
		}
		String weixinOAuthCode = request.getParameter("code");
		if (StringUtils.isNotBlank(weixinOAuthCode)) {
			logger.info(new StringBuffer("由微信客户端进入网页，授权code：").append(weixinOAuthCode).append("，待访问网址：")
					.append(request.getRequestURI()).toString());
			Optional<String> weixinOpenIdByCode = weixinConnectService.getOpenId(weixinOAuthCode);
			if (weixinOpenIdByCode.isPresent()) {
				weixinOpenId = weixinOpenIdByCode.get();
				logger.info(new StringBuffer("由微信授权code获取微信openid：").append(weixinOpenId).toString());
				request.getSession().setAttribute(WEIXIN_OPEN_ID, weixinOpenId);
			}
		}
		// 获取微信openId后，若对应用户已注册但尚未登录，执行登录操作
		/*if (weixinOpenId != null) {
			UserInfo weixinUser = userService.getValue(weixinOpenId);
			// 由于当前对象更新后redis缓存内没有清楚hashkey的对应关系，因此先在这里再做一次微信openId匹配判断
			if (weixinUser != null && weixinOpenId.equals(weixinUser.getWeixinOpenId())) {
				UserInfo currentUser = SecurityUtils.getCurrentUser();
				if (!weixinUser.equals(currentUser)) {
					if (currentUser != null) {
						org.apache.shiro.SecurityUtils.getSubject().logout();
					}
					userLoginService.userLogin(weixinUser.getMemberNumber(), weixinOpenId, null);
				}
			}
		}*/
		return true;
	}
}
