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
import com.minyisoft.webapp.yjmz.oa.model.PersonnelChangeBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.criteria.PersonnelChangeBillCriteria;
import com.minyisoft.webapp.yjmz.oa.service.PersonnelChangeBillService;
import com.minyisoft.webapp.yjmz.oa.web.view.PersonnelChangeBillExcelView;

/**
 * @author qingyong_ou 人事变动单controller
 */
@Controller
@RequestMapping("/manage")
public class PersonnelChangeBillController extends ManageBaseController {
	@Autowired
	private PersonnelChangeBillService personnelChangeBillService;

	private static final String PERMISSION_READ_ALL = "PersonnelChangeBill:readAll";// 查看全部人事变动单的权限

	/**
	 * 获取人事变动单列表
	 */
	@RequestMapping(value = "personnelChangeBillList.html", method = RequestMethod.GET)
	public String getPersonnelChangeBillList(@ModelAttribute("currentUser") UserInfo currentUser,
			@ModelAttribute("currentCompany") CompanyInfo currentCompany, PersonnelChangeBillCriteria criteria,
			Model model) throws Exception {
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
		criteria.getPageDevice().setTotalRecords(personnelChangeBillService.count(criteria));
		model.addAttribute(
				"personnelChangeBills",
				criteria.getPageDevice().getTotalRecords() == 0 ? Collections.emptyList() : personnelChangeBillService
						.getCollection(criteria));

		SelectModuleFilter filter = new SelectModuleFilter(criteria);
		filter.addField("processStatus", Arrays.asList(WorkFlowProcessStatusEnum.values()));
		filter.addField("queryBeginDate");
		filter.addField("queryEndDate");
		model.addAttribute("filter", filter);
		return "manage/personnelChangeBillList";
	}

	@ModelAttribute("personnelChangeBill")
	public PersonnelChangeBillInfo populatePersonnelChangeBill(
			@RequestParam(value = "personnelChangeBillId", required = false) PersonnelChangeBillInfo personnelChangeBill) {
		return personnelChangeBill != null ? personnelChangeBill : new PersonnelChangeBillInfo();
	}

	/**
	 * 删除人事变动单
	 */
	@RequestMapping(value = "personnelChangeBillDelete.html", method = RequestMethod.GET)
	public String deletePersonnelChangeBill(
			@ModelAttribute("personnelChangeBill") PersonnelChangeBillInfo personnelChangeBill) {
		personnelChangeBillService.delete(personnelChangeBill);
		return "redirect:personnelChangeBillList.html";
	}

	private final static String PERMISSION_CROSS_DEPARTMENT_ADD = "PersonnelChangeBill:crossDepartmentAdd";//跨部门添加人事变动单

	/**
	 * 获取人事变动单编辑界面
	 */
	@RequestMapping(value = "personnelChangeBillEdit.html", method = RequestMethod.GET)
	public String getPersonnelChangeBillEditForm(
			@ModelAttribute("personnelChangeBill") PersonnelChangeBillInfo personnelChangeBill, Model model) {
		// 人事变动单已进入工作流程，不允许编辑
		if (!personnelChangeBill.isProcessUnStarted()) {
			return "redirect:personnelChangeBillList.html";
		}
		model.addAttribute("personnelChangeBill", personnelChangeBill);
		// 是否允许为其他部门创建人事变动单
		model.addAttribute("crossDepartmentAddEnabled", PermissionUtils.hasPermission(PERMISSION_CROSS_DEPARTMENT_ADD));
		return "manage/personnelChangeBillEdit";
	}

	/**
	 * 保存编辑信息
	 */
	@RequestMapping(value = "personnelChangeBillEdit.html", method = RequestMethod.POST)
	public String processPersonnelChangeBillEditForm(@ModelAttribute("currentUser") UserInfo currentUser,
			@ModelAttribute("currentCompany") CompanyInfo currentCompany,
			@ModelAttribute("personnelChangeBill") PersonnelChangeBillInfo personnelChangeBill) {
		Optional<UserOrgRelationInfo> optionalOrgRelation = currentUser.getOrgRelation(currentCompany);
		if (!optionalOrgRelation.isPresent()) {
			return "redirect:personnelChangeBillList.html";
		}
		personnelChangeBill.setCompany(currentCompany);
		if (!PermissionUtils.hasPermission(PERMISSION_CROSS_DEPARTMENT_ADD)
				|| personnelChangeBill.getDepartment() == null) {
			personnelChangeBill.setDepartment(optionalOrgRelation.get().getDepartment());
		}
		personnelChangeBillService.submit(personnelChangeBill);
		return "redirect:personnelChangeBillList.html";
	}

	@Autowired
	private PersonnelChangeBillExcelView personnelChangeBillExcelView;

	/**
	 * 导出Excel文档
	 */
	@RequestMapping(value = "exportPersonnelChangeBillExcel.do", method = RequestMethod.GET)
	public ModelAndView exportReoprtExcel(
			@RequestParam(value = "exportIds", required = false) PersonnelChangeBillInfo[] personnelChangeBills) {
		Map<String, Object> model = Maps.newHashMap();
		model.put("personnelChangeBills", personnelChangeBills);
		return new ModelAndView(personnelChangeBillExcelView, model);
	}
}
