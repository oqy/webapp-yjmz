package com.minyisoft.webapp.yjmz.common.web;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.web.BaseController;
import com.minyisoft.webapp.yjmz.common.model.CompanyInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.service.UserService;
import com.minyisoft.webapp.yjmz.common.util.SystemConstant;

/**
 * @author qingyong_ou 用户登入登出controller
 */
@Controller
public class LoginController extends BaseController {
	@Autowired
	private UserService userService;

	/**
	 * 获取用户登录界面
	 */
	@RequestMapping(value = { "/", "login.html", "index.html" }, method = RequestMethod.GET)
	public String getLoginForm() {
		return "login";
	}

	/**
	 * 用户登录
	 */
	@RequestMapping(value = "/login.html", method = RequestMethod.POST)
	public String login(@RequestParam("userLoginName") String userLoginName,
			@RequestParam("userPassword") String userPassword, RedirectAttributes redirectAttributes) {
		if (StringUtils.hasText(userLoginName) && StringUtils.hasText(userPassword)) {
			try {
				UserInfo loginUser = userService.userLogin(userLoginName, userPassword);
				// 若是系统管理员登录，转入系统管理页面
				if (loginUser.equals(SystemConstant.ADMINISTRATOR_USER)) {
					return "redirect:admin/index.html";
				}
				// 否则进入用户管理页面
				List<CompanyInfo> optionCompanies = loginUser.getOrgList(CompanyInfo.class);
				ISystemOrgObject loginOrg = optionCompanies.isEmpty() ? null : optionCompanies.get(0);
				loginOrg = loginUser.getDefaultLoginOrg() != null ? loginUser.getDefaultLoginOrg() : loginOrg;
				if (loginOrg != null) {
					userService.currentUserSwitchOrg(loginOrg);
					return "redirect:welcome.html";
				} else {
					redirectAttributes.addFlashAttribute("errorMsg", "抱歉，您尚不隶属系统内任何公司，暂不能使用系统，请首先联系系统管理员添加组织隶属关系");
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				redirectAttributes.addFlashAttribute("errorMsg", "抱歉，您输入的用户名和密码有误，请检查");
			}
		}
		return "redirect:login.html";
	}

	/**
	 * 退出系统
	 */
	@RequestMapping(value = "/logout.html", method = RequestMethod.GET)
	public String logout() throws Exception {
		Subject currentUser = SecurityUtils.getSubject();
		if (currentUser != null) {
			currentUser.logout();
		}
		return "redirect:login";
	}
}
