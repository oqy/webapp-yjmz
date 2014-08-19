package com.minyisoft.webapp.yjmz.common.web.manage;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.minyisoft.webapp.core.web.BaseController;
import com.minyisoft.webapp.yjmz.common.security.SecurityUtils;

/**
 * @author qingyong_ou 操作平台基类controller
 * 
 */
public abstract class ManageBaseController extends BaseController {
	@ModelAttribute
	protected void prepareModel(Model model) {
		model.addAttribute("currentUser", SecurityUtils.getCurrentUser());
	}
}
