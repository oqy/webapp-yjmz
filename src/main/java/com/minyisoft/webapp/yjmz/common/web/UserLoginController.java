package com.minyisoft.webapp.yjmz.common.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.minyisoft.webapp.core.web.BaseController;

/**
 * @author qingyong_ou 用户登入登出controller
 */
@Controller
public class UserLoginController extends BaseController {
	/**
	 * 获取用户登录界面
	 */
	@RequestMapping(value = { "/", "index.html" }, method = RequestMethod.GET)
	public String getLoginForm() {
		return "signin";
	}
}
