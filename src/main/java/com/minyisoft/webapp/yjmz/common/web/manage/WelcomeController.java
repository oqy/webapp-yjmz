package com.minyisoft.webapp.yjmz.common.web.manage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.service.WorkFlowTaskService;

/**
 * @author qingyong_ou 欢迎页面controller
 */
@Controller
@RequestMapping("/manage")
public class WelcomeController extends ManageBaseController {
	@Autowired
	private WorkFlowTaskService workFlowTaskService;
	/**
	 * 获取用户登录界面
	 */
	@RequestMapping(value = "welcome.html", method = RequestMethod.GET)
	public String getWelcomePage(@ModelAttribute("currentUser") UserInfo currentUser, Model model) {
		// 待处理任务数
		model.addAttribute("todoTaskCount", workFlowTaskService.countTodoTasks(currentUser));
		return "manage/welcome";
	}
}
