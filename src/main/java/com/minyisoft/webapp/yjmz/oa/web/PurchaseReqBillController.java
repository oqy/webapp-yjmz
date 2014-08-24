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
import com.minyisoft.webapp.yjmz.oa.model.PurchaseReqBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.criteria.PurchaseReqBillCriteria;
import com.minyisoft.webapp.yjmz.oa.service.PurchaseReqBillService;

/**
 * @author qingyong_ou 采购单controller
 */
@Controller
@RequestMapping("/manage")
public class PurchaseReqBillController extends ManageBaseController {
	@Autowired
	private PurchaseReqBillService purchaseReqBillService;

	/**
	 * 获取采购单列表
	 */
	@RequestMapping(value = "purchaseReqBillList.html", method = RequestMethod.GET)
	public String getPurchaseReqBillList(@ModelAttribute("currentUser") UserInfo currentUser,
			@ModelAttribute("currentCompany") CompanyInfo currentCompany, PurchaseReqBillCriteria criteria, Model model) {
		if (criteria.getPageDevice() == null) {
			criteria.setPageDevice(new PageDevice());
		}
		criteria.setCompany(currentCompany);
		criteria.setViewer(currentUser);
		criteria.getPageDevice().setTotalRecords(purchaseReqBillService.count(criteria));
		model.addAttribute(
				"purchaseReqBills",
				criteria.getPageDevice().getTotalRecords() == 0 ? Collections.emptyList() : purchaseReqBillService
						.getCollection(criteria));

		SelectModuleFilter filter = new SelectModuleFilter(criteria);
		model.addAttribute("filter", filter);
		return "manage/purchaseReqBillList";
	}

	@ModelAttribute("purchaseReqBill")
	public PurchaseReqBillInfo populatePurchaseReqBill(
			@RequestParam(value = "purchaseReqBillId", required = false) PurchaseReqBillInfo purchaseReqBill) {
		return purchaseReqBill != null ? purchaseReqBill : new PurchaseReqBillInfo();
	}

	/**
	 * 删除采购单
	 */
	@RequestMapping(value = "purchaseReqBillDelete.html", method = RequestMethod.GET)
	public String deletePurchaseReqBill(@ModelAttribute("purchaseReqBill") PurchaseReqBillInfo purchaseReqBill) {
		purchaseReqBillService.delete(purchaseReqBill);
		return "redirect:purchaseReqBillList.html";
	}

	/**
	 * 获取采购单编辑界面
	 */
	@RequestMapping(value = "purchaseReqBillEdit.html", method = RequestMethod.GET)
	public String getPurchaseReqBillEditForm(@ModelAttribute("purchaseReqBill") PurchaseReqBillInfo purchaseReqBill,
			Model model) {
		// 工作报告已进入工作流程，不允许编辑
		if (!purchaseReqBill.isProcessUnStarted()) {
			return "redirect:purchaseReqBillList.html";
		}
		model.addAttribute("purchaseReqBill", purchaseReqBill);
		return "manage/purchaseReqBillEdit";
	}

	/**
	 * 保存编辑信息
	 */
	@RequestMapping(value = "purchaseReqBillEdit.html", method = RequestMethod.POST)
	public String processPurchaseReqBillEditForm(@ModelAttribute("currentUser") UserInfo currentUser,
			@ModelAttribute("currentCompany") CompanyInfo currentCompany,
			@ModelAttribute("purchaseReqBill") PurchaseReqBillInfo purchaseReqBill) {
		Optional<UserOrgRelationInfo> optionalOrgRelation = currentUser.getOrgRelation(currentCompany);
		if (!optionalOrgRelation.isPresent()) {
			return "redirect:purchaseReqBillList.html";
		}
		purchaseReqBill.setCompany(currentCompany);
		purchaseReqBill.setDepartment(optionalOrgRelation.get().getDepartment());
		purchaseReqBillService.submit(purchaseReqBill);
		return "redirect:purchaseReqBillList.html";
	}

	/**
	 * 获取采购单详情页
	 */
	@RequestMapping(value = "purchaseReqBillDetail.html", method = RequestMethod.GET)
	public String getPurchaseReqBillDetailPage(@ModelAttribute("purchaseReqBill") PurchaseReqBillInfo purchaseReqBill,
			Model model) {
		model.addAttribute("purchaseReqBill", purchaseReqBill);
		return "manage/purchaseReqBillDetail";
	}
}
