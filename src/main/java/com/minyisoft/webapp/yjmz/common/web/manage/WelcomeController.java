package com.minyisoft.webapp.yjmz.common.web.manage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.service.UserService;
import com.minyisoft.webapp.yjmz.common.service.WorkFlowProcessService;
import com.minyisoft.webapp.yjmz.common.service.WorkFlowTaskService;

/**
 * @author qingyong_ou 欢迎页面controller
 */
@Controller
@RequestMapping("/manage")
public class WelcomeController extends ManageBaseController {
	@Autowired
	private WorkFlowTaskService workFlowTaskService;
	@Autowired
	private WorkFlowProcessService workFlowProcessService;
	@Autowired
	private UserService userService;

	/**
	 * 获取用户登录界面
	 */
	@RequestMapping(value = { "index.html", "welcome.html" }, method = RequestMethod.GET)
	public String getWelcomePage(@ModelAttribute("currentUser") UserInfo currentUser, Model model) {
		// 待处理任务数
		model.addAttribute("todoTaskCount", workFlowTaskService.countTodoTasks(currentUser));
		// 已处理任务数
		model.addAttribute("doneTaskCount", workFlowTaskService.countDoneTasks(currentUser));
		// 由我发起正在审批中的任务数
		model.addAttribute("myRunningProcessCount",
				workFlowProcessService.countProcessInstances(currentUser));
		return "manage/welcome";
	}

	/**
	 * 切换登录组织
	 */
	@RequestMapping(value = "switchOrg.html", method = RequestMethod.GET, params = "orgId")
	public String switchOrg(@ModelAttribute("currentUser") UserInfo currentUser,
			@RequestParam("orgId") ISystemOrgObject org) {
		userService.switchOrg(currentUser, org);
		return "redirect:index.html";
	}

	/**
	 * 查看用户信息
	 */
	@RequestMapping(value = "userInfo.html", method = RequestMethod.GET)
	public String getUserInfo() {
		return "manage/userInfo";
	}

	/**
	 * 设置默认登录组织
	 */
	@RequestMapping(value = "setDefaultLoginOrg.html", method = RequestMethod.GET, params = "orgId")
	public String setDefaultLoginOrg(@ModelAttribute("currentUser") UserInfo currentUser,
			@RequestParam("orgId") ISystemOrgObject org) {
		if (currentUser.isBelongTo(org)) {
			currentUser.setDefaultLoginOrg(org);
			userService.save(currentUser);
		}
		return "redirect:userInfo.html";
	}
}
