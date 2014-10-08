package com.minyisoft.webapp.yjmz.oa.web;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.minyisoft.webapp.core.model.criteria.PageDevice;
import com.minyisoft.webapp.core.security.utils.PermissionUtils;
import com.minyisoft.webapp.core.web.utils.SelectModuleFilter;
import com.minyisoft.webapp.yjmz.common.model.CompanyInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.UserOrgRelationInfo;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowProcessStatusEnum;
import com.minyisoft.webapp.yjmz.common.web.manage.ManageBaseController;
import com.minyisoft.webapp.yjmz.oa.model.ReportInfo;
import com.minyisoft.webapp.yjmz.oa.model.criteria.ReportCriteria;
import com.minyisoft.webapp.yjmz.oa.service.ReportService;
import com.minyisoft.webapp.yjmz.oa.web.view.ReportExcelView;

/**
 * @author qingyong_ou 工作报告controller
 */
@Controller
@RequestMapping("/manage")
public class ReportController extends ManageBaseController {
	@Autowired
	private ReportService reportService;
	
	private static final String PERMISSION_READ_ALL = "Report:readAll";// 查看全部工作报告的权限

	/**
	 * 获取报告列表
	 */
	@RequestMapping(value = "reportList.html", method = RequestMethod.GET)
	public String getReportList(@ModelAttribute("currentUser") UserInfo currentUser,
			@ModelAttribute("currentCompany") CompanyInfo currentCompany, ReportCriteria criteria, Model model)
			throws Exception {
		if (criteria.getPageDevice() == null) {
			criteria.setPageDevice(new PageDevice());
		}
		if (criteria.getQueryBeginDate() != null && criteria.getQueryEndDate() != null
				&& criteria.getQueryBeginDate().after(criteria.getQueryEndDate())) {
			criteria.setQueryEndDate(criteria.getQueryBeginDate());
		}
		criteria.setCompany(currentCompany);
		if (!PermissionUtils.hasPermission(PERMISSION_READ_ALL)) {
			criteria.setViewer(currentUser);
		}
		criteria.getPageDevice().setTotalRecords(reportService.count(criteria));
		model.addAttribute("reports", criteria.getPageDevice().getTotalRecords() == 0 ? Collections.emptyList()
				: reportService.getCollection(criteria));

		SelectModuleFilter filter = new SelectModuleFilter(criteria);
		filter.addField("processStatus", Arrays.asList(WorkFlowProcessStatusEnum.values()));
		filter.addField("queryBeginDate");
		filter.addField("queryEndDate");
		model.addAttribute("filter", filter);
		return "manage/reportList";
	}

	@ModelAttribute("report")
	public ReportInfo populateReport(@RequestParam(value = "reportId", required = false) ReportInfo report) {
		return report != null ? report : new ReportInfo();
	}

	/**
	 * 删除报告
	 */
	@RequestMapping(value = "reportDelete.html", method = RequestMethod.GET)
	public String deleteReport(@ModelAttribute("report") ReportInfo report) {
		reportService.delete(report);
		return "redirect:reportList.html";
	}

	/**
	 * 获取工作报告编辑界面
	 */
	@RequestMapping(value = "reportEdit.html", method = RequestMethod.GET)
	public String getReportEditForm(@ModelAttribute("report") ReportInfo report, Model model) {
		// 工作报告已进入工作流程，不允许编辑
		if (!report.isProcessUnStarted()) {
			return "redirect:reportList.html";
		}
		model.addAttribute("report", report);
		return "manage/reportEdit";
	}

	/**
	 * 保存编辑信息
	 */
	@RequestMapping(value = "reportEdit.html", method = RequestMethod.POST)
	public String processReportEditForm(@ModelAttribute("currentUser") UserInfo currentUser,
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

	@Autowired
	private ReportExcelView reportExcelView;

	/**
	 * 导出Excel文档
	 */
	@RequestMapping(value = "exportReoprtExcel.do", method = RequestMethod.GET)
	public ModelAndView exportReoprtExcel(@RequestParam(value = "exportIds", required = false) ReportInfo[] reports) {
		Map<String, Object> model = Maps.newHashMap();
		model.put("reports", reports);
		return new ModelAndView(reportExcelView, model);
	}
}
