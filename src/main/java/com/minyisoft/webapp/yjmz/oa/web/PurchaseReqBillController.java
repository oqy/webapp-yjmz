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
import com.minyisoft.webapp.yjmz.oa.model.PurchaseReqBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.criteria.PurchaseReqBillCriteria;
import com.minyisoft.webapp.yjmz.oa.service.PurchaseReqBillService;
import com.minyisoft.webapp.yjmz.oa.web.view.PurchaseReqBillExcelView;

/**
 * @author qingyong_ou 采购单controller
 */
@Controller
@RequestMapping("/manage")
public class PurchaseReqBillController extends ManageBaseController {
	@Autowired
	private PurchaseReqBillService purchaseReqBillService;
	
	private static final String PERMISSION_READ_ALL = "PurchaseReqBill:readAll";// 查看全部采购单的权限

	/**
	 * 获取采购单列表
	 */
	@RequestMapping(value = "purchaseReqBillList.html", method = RequestMethod.GET)
	public String getPurchaseReqBillList(@ModelAttribute("currentUser") UserInfo currentUser,
			@ModelAttribute("currentCompany") CompanyInfo currentCompany, PurchaseReqBillCriteria criteria, Model model) throws Exception {
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
		criteria.getPageDevice().setTotalRecords(purchaseReqBillService.count(criteria));
		model.addAttribute(
				"purchaseReqBills",
				criteria.getPageDevice().getTotalRecords() == 0 ? Collections.emptyList() : purchaseReqBillService
						.getCollection(criteria));

		SelectModuleFilter filter = new SelectModuleFilter(criteria);
		filter.addField("processStatus", Arrays.asList(WorkFlowProcessStatusEnum.values()));
		filter.addField("queryBeginDate");
		filter.addField("queryEndDate");
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
	
	@Autowired
	private PurchaseReqBillExcelView purchaseReqBillExcelView;

	/**
	 * 导出Excel文档
	 */
	@RequestMapping(value = "exportPurchaseReqBillExcel.do", method = RequestMethod.GET)
	public ModelAndView exportReoprtExcel(@RequestParam(value = "exportIds", required = false) PurchaseReqBillInfo[] purchaseReqBills) {
		Map<String, Object> model = Maps.newHashMap();
		model.put("purchaseReqBills", purchaseReqBills);
		return new ModelAndView(purchaseReqBillExcelView, model);
	}
}
