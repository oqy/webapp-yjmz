package com.minyisoft.webapp.yjmz.common.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.minyisoft.webapp.core.web.BaseController;
import com.minyisoft.webapp.yjmz.common.model.CompanyInfo;
import com.minyisoft.webapp.yjmz.common.model.RoleInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.UserOrgRelationInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.RoleCriteria;
import com.minyisoft.webapp.yjmz.common.model.criteria.UserCriteria;
import com.minyisoft.webapp.yjmz.common.model.enumField.UserMaleEnum;
import com.minyisoft.webapp.yjmz.common.service.RoleService;
import com.minyisoft.webapp.yjmz.common.service.UserOrgRelationService;
import com.minyisoft.webapp.yjmz.common.service.UserService;
import com.minyisoft.webapp.yjmz.common.util.SystemConstant;

/**
 * @author qingyong_ou 用户组织关系controller
 */
@Controller
@RequestMapping("/admin")
public class UserOrgRelationController extends BaseController {
	@Autowired
	private UserOrgRelationService userOrgRelationService;
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;

	@ModelAttribute("userOrgRelation")
	public UserOrgRelationInfo populateUserOrgRelation(
			@RequestParam(value = "userOrgRelationId", required = false) UserOrgRelationInfo userOrgRelation) {
		return userOrgRelation != null ? userOrgRelation : new UserOrgRelationInfo();
	}

	/**
	 * 获取用户组织关系编辑界面
	 */
	@RequestMapping(value = "userOrgRelationEdit.html", method = RequestMethod.GET)
	public String getDepartmentEditForm(@RequestParam(value = "companyId", required = false) CompanyInfo company,
			@ModelAttribute("userOrgRelation") UserOrgRelationInfo userOrgRelation, Model model) {
		company = company == null ? (CompanyInfo) userOrgRelation.getOrg() : company;
		if (company == null) {
			return "redirect:companyList.html";
		}
		model.addAttribute("company", company);
		model.addAttribute("userOrgRelation", userOrgRelation);

		// 用户性别
		model.addAttribute("userMales", UserMaleEnum.values());

		// 除系统管理员外全体用户
		UserCriteria userCriteria = new UserCriteria();
		userCriteria.setExcludeIds(SystemConstant.ADMINISTATOR_USER_ID);
		model.addAttribute("users", userService.getCollection(userCriteria));

		// 公司包含角色
		RoleCriteria roleCriteria = new RoleCriteria();
		roleCriteria.setOrg(company);
		model.addAttribute("companyRoles", roleService.getCollection(roleCriteria));

		// 用户已拥有的组织角色
		if (userOrgRelation.getUser() != null) {
			model.addAttribute("userOrgRoles", userService.getUserRoles(userOrgRelation.getUser(), company));
		}
		return "admin/userOrgRelationEdit";
	}

	/**
	 * 保存编辑信息
	 */
	@RequestMapping(value = "userOrgRelationEdit.html", method = RequestMethod.POST)
	public String processDepartmentEditForm(@ModelAttribute("userOrgRelation") UserOrgRelationInfo userOrgRelation,
			@RequestParam(value = "existsUser", required = false) UserInfo existsUser,
			@RequestParam(value = "roles", required = false) RoleInfo[] roles) {
		if (existsUser != null) {
			userOrgRelation.setUser(existsUser);
		}
		userOrgRelationService.submit(userOrgRelation, roles);
		return "redirect:companyDetail.html?companyId=" + userOrgRelation.getOrg().getId();
	}

	/**
	 * 删除用户组织关系
	 */
	@RequestMapping(value = "userOrgRelationDelete.html", method = RequestMethod.GET)
	public String userOrgRelationDelete(@ModelAttribute("userOrgRelation") UserOrgRelationInfo userOrgRelation) {
		userOrgRelationService.delete(userOrgRelation);
		return "redirect:companyDetail.html?companyId=" + userOrgRelation.getOrg().getId();
	}
}
