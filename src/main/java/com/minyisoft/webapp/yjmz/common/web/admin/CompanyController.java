package com.minyisoft.webapp.yjmz.common.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

	@RequestMapping(value = "companyEdit.html", method = RequestMethod.GET)
	public String getCompanyEditForm(Model model) {
		return "admin/companyEdit";
	}
}
