package com.minyisoft.webapp.yjmz.common.web.admin;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.minyisoft.webapp.yjmz.common.model.CompanyInfo;
import com.minyisoft.webapp.yjmz.common.service.CompanyService;
import com.minyisoft.webapp.yjmz.common.web.ManageBaseController;

@Controller
@RequestMapping("/admin")
public class CompanyController extends ManageBaseController {
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
	public CompanyInfo populateCompany(HttpServletRequest request,
			@RequestParam(value = "companyId", required = false) CompanyInfo companyInfo) {
		return companyInfo != null ? companyInfo : new CompanyInfo();
	}

	/**
	 * 获取公司编辑界面
	 */
	@RequestMapping(value = "companyEdit.html", method = RequestMethod.GET)
	public String getCompanyEditForm(@ModelAttribute("company") CompanyInfo company, Model model) {
		model.addAttribute("company", company);
		return "admin/companyEdit";
	}

	/**
	 * 保存编辑信息
	 */
	@RequestMapping(value = "companyEdit.html", method = RequestMethod.POST)
	public String processCompanyEditForm(@ModelAttribute("company") CompanyInfo company) throws Exception {
		companyService.submit(company);
		return "redirect:companyList.html";
	}
}
