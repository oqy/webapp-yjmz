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
import com.minyisoft.webapp.yjmz.common.service.PermissionService;
import com.minyisoft.webapp.yjmz.common.service.RoleService;

/**
 * @author qingyong_ou 系统角色管理controller
 */
@Controller
@RequestMapping("/admin")
public class RoleController extends BaseController {
	@Autowired
	private RoleService roleService;
	@Autowired
	private PermissionService permissionService;

	@ModelAttribute("role")
	public RoleInfo populateRole(@RequestParam(value = "roleId", required = false) RoleInfo role) {
		return role != null ? role : new RoleInfo();
	}

	/**
	 * 获取系统角色编辑界面
	 */
	@RequestMapping(value = "roleEdit.html", method = RequestMethod.GET)
	public String getRoleEditForm(@RequestParam(value = "companyId", required = false) CompanyInfo company,
			@ModelAttribute("role") RoleInfo role, Model model) {
		company = company == null ? (CompanyInfo) role.getOrg() : company;
		if (company == null) {
			return "redirect:companyList.html";
		}
		model.addAttribute("company", company);
		model.addAttribute("role", role);
		model.addAttribute("avaliablePermissions", permissionService.getCollection());
		return "admin/roleEdit";
	}

	/**
	 * 保存编辑信息
	 */
	@RequestMapping(value = "roleEdit.html", method = RequestMethod.POST)
	public String processRoleEditForm(@ModelAttribute("role") RoleInfo role) {
		roleService.submit(role);
		return "redirect:companyDetail.html?companyId=" + role.getOrg().getId();
	}

	/**
	 * 删除角色
	 */
	@RequestMapping(value = "roleDelete.html", method = RequestMethod.GET)
	public String deleteRole(@ModelAttribute("role") RoleInfo role) {
		roleService.delete(role);
		return "redirect:companyDetail.html?companyId=" + role.getOrg().getId();
	}
}
