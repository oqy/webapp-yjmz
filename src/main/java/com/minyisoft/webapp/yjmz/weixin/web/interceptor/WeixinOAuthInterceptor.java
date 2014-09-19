package com.minyisoft.webapp.yjmz.weixin.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.base.Optional;
import com.minyisoft.webapp.weixin.common.service.WeixinCommonService;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.security.SecurityUtils;
import com.minyisoft.webapp.yjmz.common.service.UserService;

/**
 * @author qingyong_ou 微信网页授权获取用户基本信息拦截器
 */
public class WeixinOAuthInterceptor extends HandlerInterceptorAdapter {
	private Logger logger = LoggerFactory.getLogger(getClass());
	public final static String WEIXIN_OPEN_ID = "weixinOpenId";
	private final static String WEIXIN_TICKET = "weixinTicket";// 微信ticket
																// url参数名
	@Autowired
	private WeixinCommonService weixinCommonService;
	@Autowired
	private UserService userService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String weixinOpenId = request.getParameter(WEIXIN_OPEN_ID);
		if (StringUtils.isNotBlank(weixinOpenId)) {
			logger.info(new StringBuffer("由微信客户端进入网页，微信openId：").append(weixinOpenId).append("，待访问网址：")
					.append(request.getRequestURI()).toString());

		} else if (StringUtils.isNotBlank(request.getParameter(WEIXIN_TICKET))) {
			Optional<String> optionalWeixinOpenId = weixinCommonService.getOpenIdByTicket(request
					.getParameter(WEIXIN_TICKET));
			if (optionalWeixinOpenId.isPresent()) {
				weixinOpenId = optionalWeixinOpenId.get();
				logger.info(new StringBuffer("由微信ticket获取微信openid：").append(weixinOpenId).toString());
			}
		} else if (StringUtils.isNotBlank(request.getParameter("code"))) {
			String weixinOAuthCode = request.getParameter("code");
			logger.info(new StringBuffer("由微信客户端进入网页，授权code：").append(weixinOAuthCode).append("，待访问网址：")
					.append(request.getRequestURI()).toString());
			Optional<String> optionalWeixinOpenId = weixinCommonService.getOpenIdByOAuthCode(weixinOAuthCode);
			if (optionalWeixinOpenId.isPresent()) {
				weixinOpenId = optionalWeixinOpenId.get();
				logger.info(new StringBuffer("由微信授权code获取微信openid：").append(weixinOpenId).toString());
			}
		}
		// 获取微信openId后，若对应用户已注册但尚未登录，执行登录操作
		if (weixinOpenId != null) {
			request.getSession().setAttribute(WEIXIN_OPEN_ID, weixinOpenId);

			UserInfo weixinUser = userService.getValue(weixinOpenId);
			// 由于当前对象更新后redis缓存内没有清楚hashkey的对应关系，因此先在这里再做一次微信openId匹配判断
			if (weixinUser != null && weixinOpenId.equals(weixinUser.getWeixinOpenId())) {
				UserInfo currentUser = SecurityUtils.getCurrentUser();
				if (!weixinUser.equals(currentUser)) {
					if (currentUser != null) {
						org.apache.shiro.SecurityUtils.getSubject().logout();
					}
					userService.userLogin(weixinUser.getUserLoginName(), weixinOpenId);
				}
			}
		}
		return true;
	}

	/**
	 * 指定url添加微信ticket信息
	 */
	public static String appendWeixinTicket(String url, String ticket) {
		Assert.hasText(url);
		Assert.hasText(ticket);
		StringBuffer appendUrl = new StringBuffer(url);
		if (url.indexOf('?') >= 0) {
			appendUrl.append("&");
		} else {
			appendUrl.append("?");
		}
		return appendUrl.append(WEIXIN_TICKET).append("=").append(ticket).toString();
	}
}
