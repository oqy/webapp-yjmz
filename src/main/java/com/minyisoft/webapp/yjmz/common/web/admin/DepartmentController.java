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
import com.minyisoft.webapp.yjmz.common.model.DepartmentInfo;
import com.minyisoft.webapp.yjmz.common.service.DepartmentService;

/**
 * @author qingyong_ou 部门管理controller
 */
@Controller
@RequestMapping("/admin")
public class DepartmentController extends BaseController {
	@Autowired
	private DepartmentService departmentService;

	@ModelAttribute("department")
	public DepartmentInfo populateDepartment(
			@RequestParam(value = "departmentId", required = false) DepartmentInfo department) {
		return department != null ? department : new DepartmentInfo();
	}

	/**
	 * 获取部门编辑界面
	 */
	@RequestMapping(value = "departmentEdit.html", method = RequestMethod.GET)
	public String getDepartmentEditForm(@RequestParam(value = "companyId", required = false) CompanyInfo company,
			@ModelAttribute("department") DepartmentInfo department, Model model) {
		company = company == null ? (CompanyInfo) department.getOrg() : company;
		if (company == null) {
			return "redirect:companyList.html";
		}
		model.addAttribute("company", company);
		model.addAttribute("department", department);
		return "admin/departmentEdit";
	}

	/**
	 * 保存编辑信息
	 */
	@RequestMapping(value = "departmentEdit.html", method = RequestMethod.POST)
	public String processDepartmentEditForm(@ModelAttribute("department") DepartmentInfo department) {
		departmentService.submit(department);
		return "redirect:companyDetail.html?companyId=" + department.getOrg().getId();
	}

	/**
	 * 删除部门
	 */
	@RequestMapping(value = "departmentDelete.html", method = RequestMethod.GET)
	public String deleteDepartment(@ModelAttribute("department") DepartmentInfo department) {
		departmentService.delete(department);
		return "redirect:companyDetail.html?companyId=" + department.getOrg().getId();
	}
}
