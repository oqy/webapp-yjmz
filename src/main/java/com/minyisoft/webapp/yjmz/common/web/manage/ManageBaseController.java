package com.minyisoft.webapp.yjmz.common.web.manage;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.google.common.base.Optional;
import com.minyisoft.webapp.core.exception.WebException;
import com.minyisoft.webapp.core.web.BaseController;
import com.minyisoft.webapp.yjmz.common.model.CompanyInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.UserOrgRelationInfo;
import com.minyisoft.webapp.yjmz.common.security.SecurityUtils;

/**
 * @author qingyong_ou 操作平台基类controller
 * 
 */
public abstract class ManageBaseController extends BaseController {
	@ModelAttribute
	protected void prepareModel(Model model) {
		UserInfo currentUser = SecurityUtils.getCurrentUser();
		CompanyInfo currentCompany = SecurityUtils.getCurrentCompany();
		Optional<UserOrgRelationInfo> orgRelation = currentUser.getOrgRelation(currentCompany);
		if (!orgRelation.isPresent()) {
			throw new WebException("当前登录用户不允许登录指定公司");
		}

		model.addAttribute("currentUser", currentUser);
		model.addAttribute("currentCompany", currentCompany);
		model.addAttribute("currentDepartment", orgRelation.get().getDepartment());
	}
}
