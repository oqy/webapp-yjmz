package com.minyisoft.webapp.yjmz.oa.web;

import java.util.Collections;

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
import com.minyisoft.webapp.yjmz.oa.model.MaintainReqBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.criteria.MaintainReqBillCriteria;
import com.minyisoft.webapp.yjmz.oa.model.enumField.MaintainTypeEnum;
import com.minyisoft.webapp.yjmz.oa.service.MaintainReqBillService;

/**
 * @author qingyong_ou 维修通知单controller
 */
@Controller
@RequestMapping("/manage")
public class MaintainReqBillController extends ManageBaseController {
	@Autowired
	private MaintainReqBillService maintainReqBillService;

	/**
	 * 获取维修单列表
	 */
	@RequestMapping(value = "maintainReqBillList.html", method = RequestMethod.GET)
	public String getMaintainReqBillList(@ModelAttribute("currentUser") UserInfo currentUser,
			@ModelAttribute("currentCompany") CompanyInfo currentCompany, MaintainReqBillCriteria criteria, Model model) {
		if (criteria.getPageDevice() == null) {
			criteria.setPageDevice(new PageDevice());
		}
		criteria.setCompany(currentCompany);
		criteria.setViewer(currentUser);
		criteria.getPageDevice().setTotalRecords(maintainReqBillService.count(criteria));
		model.addAttribute(
				"maintainReqBills",
				criteria.getPageDevice().getTotalRecords() == 0 ? Collections.emptyList() : maintainReqBillService
						.getCollection(criteria));

		SelectModuleFilter filter = new SelectModuleFilter(criteria);
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
		return "redirect:maintainReqBillList.html";
	}

	/**
	 * 获取工作报告编辑界面
	 */
	@RequestMapping(value = "maintainReqBillEdit.html", method = RequestMethod.GET)
	public String getMaintainReqBillEditForm(@ModelAttribute("maintainReqBill") MaintainReqBillInfo maintainReqBill,
			Model model) {
		// 维修通知单已进入工作流程，不允许编辑
		if (!maintainReqBill.isProcessUnStarted()) {
			return "redirect:maintainReqBillList.html";
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
			return "redirect:maintainReqBillList.html";
		}
		maintainReqBill.setCompany(currentCompany);
		maintainReqBill.setDepartment(optionalOrgRelation.get().getDepartment());
		maintainReqBillService.submit(maintainReqBill);
		return "redirect:maintainReqBillList.html";
	}

	/**
	 * 获取维修单详情页
	 */
	@RequestMapping(value = "maintainReqBillDetail.html", method = RequestMethod.GET)
	public String getMaintainReqBillDetailPage(@ModelAttribute("maintainReqBill") MaintainReqBillInfo maintainReqBill,
			Model model) {
		model.addAttribute("maintainReqBill", maintainReqBill);
		return "manage/maintainReqBillDetail";
	}
}
