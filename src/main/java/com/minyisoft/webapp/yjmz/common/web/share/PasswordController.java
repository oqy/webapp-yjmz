package com.minyisoft.webapp.yjmz.common.web.share;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.minyisoft.webapp.core.web.BaseController;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.security.SecurityUtils;
import com.minyisoft.webapp.yjmz.common.service.UserService;

/**
 * @author qingyong_ou 重置密码controller
 */
@Controller
@RequestMapping("/share")
public class PasswordController extends BaseController {
	@Autowired
	private UserService userService;

	@ModelAttribute
	protected void prepareModel(Model model) {
		model.addAttribute("currentUser", SecurityUtils.getCurrentUser());
	}

	/**
	 * 获取重置密码页面
	 */
	@RequestMapping(value = "passwordReset.html", method = RequestMethod.GET)
	public String getPasswordResetForm(Model model) {
		model.addAttribute("currentCompany", SecurityUtils.getCurrentCompany());
		return "share/passwordReset";
	}

	/**
	 * 处理重置密码
	 */
	@RequestMapping(value = "passwordReset.html", method = RequestMethod.POST)
	public String processPasswordResetForm(@ModelAttribute("currentUser") UserInfo currentUser,
			@RequestParam String oldPassword, @RequestParam String newPassword, RedirectAttributes redirectAttributes) {
		if (!currentUser.isPasswordCorrect(oldPassword)) {
			redirectAttributes.addFlashAttribute("success", false);
			redirectAttributes.addFlashAttribute("msg", "原密码不正确，请检查");
		} else {
			currentUser.constructUserPassword(newPassword);
			userService.save(currentUser);
			redirectAttributes.addFlashAttribute("success", true);
			redirectAttributes.addFlashAttribute("msg", "登录密码已修改，请牢记新登录密码");
		}
		return "redirect:passwordReset.html";
	}
}
