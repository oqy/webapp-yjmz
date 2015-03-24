package com.minyisoft.webapp.yjmz.oa.web;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minyisoft.webapp.core.model.IBillObject;
import com.minyisoft.webapp.core.model.criteria.PageDevice;
import com.minyisoft.webapp.core.security.utils.PermissionUtils;
import com.minyisoft.webapp.core.web.utils.SelectModuleFilter;
import com.minyisoft.webapp.yjmz.common.model.CompanyInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.UserOrgRelationInfo;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowProcessStatusEnum;
import com.minyisoft.webapp.yjmz.common.web.manage.ManageBaseController;
import com.minyisoft.webapp.yjmz.oa.model.MaintainReqBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.MaintainReqEntryInfo;
import com.minyisoft.webapp.yjmz.oa.model.PurchaseReqBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.PurchaseReqEntryInfo;
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
			@ModelAttribute("currentCompany") CompanyInfo currentCompany, PurchaseReqBillCriteria criteria, Model model)
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
		criteria.getPageDevice().setTotalRecords(purchaseReqBillService.count(criteria));
		model.addAttribute(
				"purchaseReqBills",
				criteria.getPageDevice().getTotalRecords() == 0 ? Collections.emptyList() : purchaseReqBillService
						.getCollection(criteria));
		model.addAttribute("processStatuses", WorkFlowProcessStatusEnum.values());

		SelectModuleFilter filter = new SelectModuleFilter(criteria);
		filter.addHiddenField("processStatus");
		filter.addField("queryBeginDate");
		filter.addField("queryEndDate");
		model.addAttribute("filter", filter);
		return "manage/purchaseReqBillList";
	}

	@ModelAttribute("purchaseReqBill")
	public PurchaseReqBillInfo populatePurchaseReqBill(HttpServletRequest request,
			@RequestParam(value = "purchaseReqBillId", required = false) PurchaseReqBillInfo purchaseReqBill) {
		purchaseReqBill = purchaseReqBill != null ? purchaseReqBill : new PurchaseReqBillInfo();
		if (StringUtils.equalsIgnoreCase(request.getMethod(), "post")) {
			purchaseReqBill.setEntry(null);
		}
		return purchaseReqBill;
	}

	/**
	 * 删除采购单
	 */
	@RequestMapping(value = "purchaseReqBillDelete.html", method = RequestMethod.GET)
	public String deletePurchaseReqBill(@ModelAttribute("purchaseReqBill") PurchaseReqBillInfo purchaseReqBill) {
		purchaseReqBillService.delete(purchaseReqBill);
		return "redirect:purchaseReqBillList.html?processStatus=" + purchaseReqBill.getProcessStatus().getValue();
	}

	/**
	 * 获取采购单编辑界面
	 */
	@RequestMapping(value = "purchaseReqBillEdit.html", method = RequestMethod.GET)
	public String getPurchaseReqBillEditForm(@ModelAttribute("purchaseReqBill") PurchaseReqBillInfo purchaseReqBill,
			@RequestParam(value = "sourceBill", required = false) IBillObject sourceBill, Model model) {
		// 采购单已进入工作流程，不允许编辑
		if (!purchaseReqBill.isProcessUnStarted()) {
			return "redirect:purchaseReqBillList.html?processStatus=" + purchaseReqBill.getProcessStatus().getValue();
		}
		model.addAttribute("purchaseReqBill", purchaseReqBill);
		model.addAttribute("sourceBill", sourceBill);
		// 源单为工程维修单，将维修单分录转为采购单分录
		if (sourceBill instanceof MaintainReqBillInfo
				&& CollectionUtils.isNotEmpty(((MaintainReqBillInfo) sourceBill).getEntry())
				&& CollectionUtils.isEmpty(purchaseReqBill.getEntry())) {
			List<PurchaseReqEntryInfo> entry = Lists.newArrayList();
			PurchaseReqEntryInfo purchaseReqEntry = null;
			for (MaintainReqEntryInfo maintainReqEntry : ((MaintainReqBillInfo) sourceBill).getEntry()) {
				purchaseReqEntry = new PurchaseReqEntryInfo();
				purchaseReqEntry.setName(maintainReqEntry.getName());
				purchaseReqEntry.setQuantity(maintainReqEntry.getQuantity());
				purchaseReqEntry.setUnitPrice(maintainReqEntry.getPrice());
				entry.add(purchaseReqEntry);
			}
			purchaseReqBill.setEntry(entry);
		}
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
			return "redirect:purchaseReqBillList.html?processStatus=" + purchaseReqBill.getProcessStatus().getValue();
		}
		purchaseReqBill.setCompany(currentCompany);
		purchaseReqBill.setDepartment(optionalOrgRelation.get().getDepartment());
		purchaseReqBillService.submit(purchaseReqBill);
		return "redirect:purchaseReqBillList.html?processStatus=" + purchaseReqBill.getProcessStatus().getValue();
	}

	@Autowired
	private PurchaseReqBillExcelView purchaseReqBillExcelView;

	/**
	 * 导出Excel文档
	 */
	@RequestMapping(value = "exportPurchaseReqBillExcel.do", method = RequestMethod.GET)
	public ModelAndView exportReoprtExcel(
			@RequestParam(value = "exportIds", required = false) PurchaseReqBillInfo[] purchaseReqBills) {
		Map<String, Object> model = Maps.newHashMap();
		model.put("purchaseReqBills", purchaseReqBills);
		return new ModelAndView(purchaseReqBillExcelView, model);
	}
}
