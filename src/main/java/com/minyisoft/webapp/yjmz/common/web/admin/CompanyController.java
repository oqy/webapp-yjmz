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
import com.minyisoft.webapp.yjmz.common.model.enumField.CompanyStatusEnum;
import com.minyisoft.webapp.yjmz.common.service.CompanyService;

/**
 * @author qingyong_ou 公司管理controller
 */
@Controller
@RequestMapping("/admin")
public class CompanyController extends BaseController {
	@Autowired
	private CompanyService companyService;

	/**
	 * 获取公司列表
	 */
	@RequestMapping(value = "companyList.html", method = RequestMethod.GET)
	public String getCompanyList(Model model) {
		model.addAttribute("companies", companyService.getCollection());
		return "admin/companyList";
	}

	@ModelAttribute("company")
	public CompanyInfo populateCompany(@RequestParam(value = "companyId", required = false) CompanyInfo company) {
		return company != null ? company : new CompanyInfo();
	}

	/**
	 * 获取公司编辑界面
	 */
	@RequestMapping(value = "companyEdit.html", method = RequestMethod.GET)
	public String getCompanyEditForm(@ModelAttribute("company") CompanyInfo company, Model model) {
		model.addAttribute("company", company);
		model.addAttribute("companyStatus", CompanyStatusEnum.values());
		return "admin/companyEdit";
	}

	/**
	 * 保存编辑信息
	 */
	@RequestMapping(value = "companyEdit.html", method = RequestMethod.POST)
	public String processCompanyEditForm(@ModelAttribute("company") CompanyInfo company){
		companyService.submit(company);
		return "redirect:companyList.html";
	}

	/**
	 * 获取公司详情页
	 */
	@RequestMapping(value = "companyDetail.html", method = RequestMethod.GET)
	public String getCompanyDetailPage(@ModelAttribute("company") CompanyInfo company, Model model) {
		model.addAttribute("company", company);
		return "admin/companyDetail";
	}
}
