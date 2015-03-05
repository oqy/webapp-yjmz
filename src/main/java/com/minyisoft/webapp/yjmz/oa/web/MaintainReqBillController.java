package com.minyisoft.webapp.yjmz.oa.web;

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
import com.minyisoft.webapp.yjmz.oa.model.MaintainReqBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.criteria.MaintainReqBillCriteria;
import com.minyisoft.webapp.yjmz.oa.model.enumField.MaintainTypeEnum;
import com.minyisoft.webapp.yjmz.oa.service.MaintainReqBillService;
import com.minyisoft.webapp.yjmz.oa.web.view.MaintainReqBillExcelView;

/**
 * @author qingyong_ou 维修通知单controller
 */
@Controller
@RequestMapping("/manage")
public class MaintainReqBillController extends ManageBaseController {
	@Autowired
	private MaintainReqBillService maintainReqBillService;

	private static final String PERMISSION_READ_ALL = "MaintainReqBill:readAll";// 查看全部维修单的权限

	/**
	 * 获取维修单列表
	 */
	@RequestMapping(value = "maintainReqBillList.html", method = RequestMethod.GET)
	public String getMaintainReqBillList(@ModelAttribute("currentUser") UserInfo currentUser,
			@ModelAttribute("currentCompany") CompanyInfo currentCompany, MaintainReqBillCriteria criteria, Model model)
			throws Exception {
		if (criteria.getPageDevice() == null) {
			criteria.setPageDevice(new PageDevice());
		}
		if (criteria.getQueryBeginDate() != null && criteria.getQueryEndDate() != null
				&& criteria.getQueryBeginDate().after(criteria.getQueryEndDate())) {
			criteria.setQueryEndDate(criteria.getQueryBeginDate());
		}
		if (criteria.getProcessStatus() == null) {
			criteria.setProcessStatus(WorkFlowProcessStatusEnum.RUNNING);
		}
		criteria.setCompany(currentCompany);
		if (!PermissionUtils.hasPermission(PERMISSION_READ_ALL)) {
			criteria.setViewer(currentUser);
		}
		criteria.getPageDevice().setTotalRecords(maintainReqBillService.count(criteria));
		model.addAttribute(
				"maintainReqBills",
				criteria.getPageDevice().getTotalRecords() == 0 ? Collections.emptyList() : maintainReqBillService
						.getCollection(criteria));
		model.addAttribute("processStatuses", WorkFlowProcessStatusEnum.values());

		SelectModuleFilter filter = new SelectModuleFilter(criteria);
		filter.addHiddenField("processStatus");
		filter.addField("queryBeginDate");
		filter.addField("queryEndDate");
		model.addAttribute("filter", filter);
		return "manage/maintainReqBillList";
	}

	@ModelAttribute("maintainReqBill")
	public MaintainReqBillInfo populateMaintainReqBill(
			@RequestParam(value = "maintainReqBillId", required = false) MaintainReqBillInfo maintainReqBill) {
		return maintainReqBill != null ? maintainReqBill : new MaintainReqBillInfo();
	}

	/**
	 * 删除维修单
	 */
	@RequestMapping(value = "maintainReqBillDelete.html", method = RequestMethod.GET)
	public String deleteMaintainReqBill(@ModelAttribute("maintainReqBill") MaintainReqBillInfo maintainReqBill) {
		maintainReqBillService.delete(maintainReqBill);
		return "redirect:maintainReqBillList.html?processStatus=" + maintainReqBill.getProcessStatus().getValue();
	}

	/**
	 * 获取工作报告编辑界面
	 */
	@RequestMapping(value = "maintainReqBillEdit.html", method = RequestMethod.GET)
	public String getMaintainReqBillEditForm(@ModelAttribute("maintainReqBill") MaintainReqBillInfo maintainReqBill,
			Model model) {
		// 维修通知单已进入工作流程，不允许编辑
		if (!maintainReqBill.isProcessUnStarted()) {
			return "redirect:maintainReqBillList.html?processStatus=" + maintainReqBill.getProcessStatus().getValue();
		}
		model.addAttribute("maintainReqBill", maintainReqBill);
		// 维修类型
		model.addAttribute("maintainTypes", MaintainTypeEnum.values());
		return "manage/maintainReqBillEdit";
	}

	/**
	 * 保存编辑信息
	 */
	@RequestMapping(value = "maintainReqBillEdit.html", method = RequestMethod.POST)
	public String processMaintainReqBillEditForm(@ModelAttribute("currentUser") UserInfo currentUser,
			@ModelAttribute("currentCompany") CompanyInfo currentCompany,
			@ModelAttribute("maintainReqBill") MaintainReqBillInfo maintainReqBill) {
		Optional<UserOrgRelationInfo> optionalOrgRelation = currentUser.getOrgRelation(currentCompany);
		if (!optionalOrgRelation.isPresent()) {
			return "redirect:maintainReqBillList.html?processStatus=" + maintainReqBill.getProcessStatus().getValue();
		}
		maintainReqBill.setCompany(currentCompany);
		maintainReqBill.setDepartment(optionalOrgRelation.get().getDepartment());
		maintainReqBillService.submit(maintainReqBill);
		return "redirect:maintainReqBillList.html?processStatus=" + maintainReqBill.getProcessStatus().getValue();
	}

	@Autowired
	private MaintainReqBillExcelView maintainReqBillExcelView;

	/**
	 * 导出Excel文档
	 */
	@RequestMapping(value = "exportMaintainReqBillExcel.do", method = RequestMethod.GET)
	public ModelAndView exportReoprtExcel(
			@RequestParam(value = "exportIds", required = false) MaintainReqBillInfo[] maintainReqBills) {
		Map<String, Object> model = Maps.newHashMap();
		model.put("maintainReqBills", maintainReqBills);
		return new ModelAndView(maintainReqBillExcelView, model);
	}
}
