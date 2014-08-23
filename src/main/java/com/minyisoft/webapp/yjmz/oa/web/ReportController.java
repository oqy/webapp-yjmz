package com.minyisoft.webapp.yjmz.oa.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Optional;
import com.minyisoft.webapp.core.model.criteria.PageDevice;
import com.minyisoft.webapp.core.web.utils.SelectModuleFilter;
import com.minyisoft.webapp.yjmz.common.model.CompanyInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.UserOrgRelationInfo;
import com.minyisoft.webapp.yjmz.common.web.manage.ManageBaseController;
import com.minyisoft.webapp.yjmz.oa.model.ReportInfo;
import com.minyisoft.webapp.yjmz.oa.model.criteria.ReportCriteria;
import com.minyisoft.webapp.yjmz.oa.service.ReportService;

/**
 * @author qingyong_ou 工作报告controller
 */
@Controller
@RequestMapping("/manage")
public class ReportController extends ManageBaseController {
	@Autowired
	private ReportService reportService;

	/**
	 * 获取报告列表
	 */
	@RequestMapping(value = "reportList.html", method = RequestMethod.GET)
	public String getReportList(@ModelAttribute("currentUser") UserInfo currentUser, ReportCriteria criteria,
			Model model) {
		if (criteria.getPageDevice() == null) {
			criteria.setPageDevice(new PageDevice());
		}
		criteria.setViewer(currentUser);
		criteria.getPageDevice().setTotalRecords(reportService.count(criteria));
		model.addAttribute("reports", reportService.getCollection(criteria));
		
		SelectModuleFilter filter = new SelectModuleFilter(criteria);
		model.addAttribute("filter", filter);
		return "manage/reportList";
	}

	@ModelAttribute("report")
	public ReportInfo populateReport(@RequestParam(value = "reportId", required = false) ReportInfo report) {
		return report != null ? report : new ReportInfo();
	}

	/**
	 * 获取工作报告编辑界面
	 */
	@RequestMapping(value = "reportEdit.html", method = RequestMethod.GET)
	public String getReportEditForm(@ModelAttribute("report") ReportInfo report, Model model) {
		// 工作报告已进入工作流程，不允许编辑
		if (report.isIdPresented() && StringUtils.isNotBlank(report.getProcessInstanceId())) {
			return "redirect:reportList.html";
		}
		model.addAttribute("report", report);
		return "manage/reportEdit";
	}

	/**
	 * 保存编辑信息
	 */
	@RequestMapping(value = "reportEdit.html", method = RequestMethod.POST)
	public String processCompanyEditForm(@ModelAttribute("currentUser") UserInfo currentUser,
			@ModelAttribute("currentCompany") CompanyInfo currentCompany, @ModelAttribute("report") ReportInfo report) {
		Optional<UserOrgRelationInfo> optionalOrgRelation = currentUser.getOrgRelation(currentCompany);
		if (!optionalOrgRelation.isPresent()) {
			return "redirect:reportList.html";
		}
		report.setCompany(currentCompany);
		report.setDepartment(optionalOrgRelation.get().getDepartment());
		reportService.submit(report);
		return "redirect:reportList.html";
	}

	/**
	 * 获取报告详情页
	 */
	@RequestMapping(value = "reportDetail.html", method = RequestMethod.GET)
	public String getReportDetailPage(@ModelAttribute("report") ReportInfo report, Model model) {
		model.addAttribute("report", report);
		return "manage/reportDetail";
	}
}
