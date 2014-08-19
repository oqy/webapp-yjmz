package com.minyisoft.webapp.yjmz.common.web.manage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class WelcomeController extends ManageBaseController {
	/**
	 * 获取用户登录界面
	 */
	@RequestMapping(value = { "/welcome.html" }, method = RequestMethod.GET)
	public String getWelcomePage() {
		return "welcome";
	}
}
