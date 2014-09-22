package com.minyisoft.webapp.yjmz.common.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.minyisoft.webapp.core.model.criteria.PageDevice;
import com.minyisoft.webapp.core.web.BaseController;
import com.minyisoft.webapp.core.web.utils.SelectModuleFilter;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.UserCriteria;
import com.minyisoft.webapp.yjmz.common.model.enumField.UserMaleEnum;
import com.minyisoft.webapp.yjmz.common.model.enumField.UserStatusEnum;
import com.minyisoft.webapp.yjmz.common.service.UserService;
import com.minyisoft.webapp.yjmz.common.util.SystemConstant;

/**
 * @author qingyong_ou 系统用户管理controller
 */
@Controller
@RequestMapping("/admin")
public class UserController extends BaseController {
	@Autowired
	private UserService userService;

	/**
	 * 获取系统用户列表
	 */
	@RequestMapping(value = "userList.html", method = RequestMethod.GET)
	public String getUserList(UserCriteria userCriteria, Model model) {
		if (userCriteria.getPageDevice() == null) {
			userCriteria.setPageDevice(new PageDevice());
		}
		userCriteria.setExcludeIds(SystemConstant.ADMINISTATOR_USER_ID);
		userCriteria.getPageDevice().setTotalRecords(userService.count(userCriteria));
		model.addAttribute("users", userService.getCollection(userCriteria));

		SelectModuleFilter filter = new SelectModuleFilter(userCriteria);
		model.addAttribute("filter", filter);
		return "admin/userList";
	}

	@ModelAttribute("user")
	public UserInfo populateUser(@RequestParam(value = "userId", required = false) UserInfo user) {
		return user != null ? user : new UserInfo();
	}

	/**
	 * 获取用户编辑界面
	 */
	@RequestMapping(value = "userEdit.html", method = RequestMethod.GET)
	public String getUserEditForm(@ModelAttribute("user") UserInfo user, Model model) {
		model.addAttribute("user", user);
		model.addAttribute("userMales", UserMaleEnum.values());
		model.addAttribute("userStatus", UserStatusEnum.values());
		return "admin/userEdit";
	}

	/**
	 * 保存编辑信息
	 */
	@RequestMapping(value = "userEdit.html", method = RequestMethod.POST)
	public String processUserEditForm(@ModelAttribute("user") UserInfo user) {
		userService.submit(user);
		return "redirect:userList.html";
	}

	/**
	 * 重置用户登录密码
	 */
	@RequestMapping(value = "userPasswordReset.html", method = RequestMethod.GET)
	public String resetUserPassword(@ModelAttribute("user") UserInfo user) {
		user.constructUserPassword(user.getCellPhoneNumber());
		userService.submit(user);
		return "redirect:userList.html";
	}
}
