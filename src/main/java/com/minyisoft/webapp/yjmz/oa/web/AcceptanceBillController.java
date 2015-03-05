package com.minyisoft.webapp.yjmz.oa.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Optional;
import com.minyisoft.webapp.core.model.IBillObject;
import com.minyisoft.webapp.yjmz.common.model.CompanyInfo;
import com.minyisoft.webapp.yjmz.common.model.CompanyWorkFlowBillBaseInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.UserOrgRelationInfo;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowProcessStatusEnum;
import com.minyisoft.webapp.yjmz.common.web.manage.ManageBaseController;
import com.minyisoft.webapp.yjmz.oa.model.AcceptanceBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.ReportInfo;
import com.minyisoft.webapp.yjmz.oa.service.AcceptanceBillService;

/**
 * @author qingyong_ou 工作报告controller
 */
@Controller
@RequestMapping("/manage")
public class AcceptanceBillController extends ManageBaseController {
	@Autowired
	private AcceptanceBillService acceptanceBillService;

	/**
	 * 获取工作报告编辑界面
	 */
	@RequestMapping(value = "acceptanceBillEdit.html", method = RequestMethod.GET)
	public String getAcceptanceBillForm(
			@RequestParam(value = "sourceBill", required = false) CompanyWorkFlowBillBaseInfo sourceBill, Model model) {
		// 原单未完成审批流程，不允许提交验收申请
		if (sourceBill == null || sourceBill.getProcessStatus() != WorkFlowProcessStatusEnum.FINISHED) {
			return _getRedirectUrl(sourceBill);
		}
		model.addAttribute("sourceBill", sourceBill);
		return "manage/acceptanceBillEdit";
	}

	/**
	 * 保存编辑信息
	 */
	@RequestMapping(value = "acceptanceBillEdit.html", method = RequestMethod.POST)
	public String processAcceptanceBillForm(@ModelAttribute("currentUser") UserInfo currentUser,
			@ModelAttribute("currentCompany") CompanyInfo currentCompany, AcceptanceBillInfo acceptanceBill) {
		Optional<UserOrgRelationInfo> optionalOrgRelation = currentUser.getOrgRelation(currentCompany);
		if (!optionalOrgRelation.isPresent()) {
			return _getRedirectUrl(acceptanceBill.getSourceBill());
		}
		acceptanceBill.setCompany(currentCompany);
		acceptanceBill.setDepartment(optionalOrgRelation.get().getDepartment());
		acceptanceBillService.submit(acceptanceBill);
		return _getRedirectUrl(acceptanceBill.getSourceBill());
	}

	/**
	 * 获取重定向连接
	 */
	private String _getRedirectUrl(IBillObject sourceBill) {
		if (sourceBill instanceof ReportInfo) {
			return "redirect:reportList.html?processStatus=" + ((ReportInfo) sourceBill).getProcessStatus().getValue();
		} else {
			return "redirect:index.html";
		}
	}
}
