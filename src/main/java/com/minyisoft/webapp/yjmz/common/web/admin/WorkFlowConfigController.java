package com.minyisoft.webapp.yjmz.common.web.admin;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Optional;
import com.minyisoft.webapp.core.model.criteria.PageDevice;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.core.web.BaseController;
import com.minyisoft.webapp.core.web.utils.SelectModuleFilter;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowConfigInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.CompanyCriteria;
import com.minyisoft.webapp.yjmz.common.model.criteria.WorkFlowConfigCriteria;
import com.minyisoft.webapp.yjmz.common.model.enumField.CompanyStatusEnum;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowStatusEnum;
import com.minyisoft.webapp.yjmz.common.service.CompanyService;
import com.minyisoft.webapp.yjmz.common.service.WorkFlowConfigService;
import com.minyisoft.webapp.yjmz.common.util.workflow.ActivitiHelper;
import com.minyisoft.webapp.yjmz.common.util.workflow.ActivitiHelper.ProcessResourceType;

/**
 * @author qingyong_ou 工作流流程定义配置信息
 */
@Controller
@RequestMapping("/admin")
public class WorkFlowConfigController extends BaseController {
	@Autowired
	private WorkFlowConfigService workFlowConfigService;
	@Autowired
	private CompanyService companyService;

	/**
	 * 获取工作流流程定义配置信息
	 */
	@RequestMapping(value = "workFlowConfigList.html", method = RequestMethod.GET)
	public String getWorkFlowConfigList(WorkFlowConfigCriteria criteria, Model model) throws Exception {
		if (criteria.getPageDevice() == null) {
			criteria.setPageDevice(new PageDevice());
		}
		criteria.getPageDevice().setTotalRecords(workFlowConfigService.count(criteria));
		model.addAttribute("workFlowConfigs", criteria.getPageDevice().getTotalRecords() == 0 ? Collections.emptyList()
				: workFlowConfigService.getCollection(criteria));

		SelectModuleFilter filter = new SelectModuleFilter(criteria);
		filter.addField("workFlowStatus", Arrays.asList(WorkFlowStatusEnum.values()));
		model.addAttribute("filter", filter);
		return "admin/workFlowConfigList";
	}

	@ModelAttribute("workFlowConfig")
	public WorkFlowConfigInfo populateWorkFlowConfig(
			@RequestParam(value = "workFlowConfigId", required = false) WorkFlowConfigInfo config) {
		return config != null ? config : new WorkFlowConfigInfo();
	}

	/**
	 * 删除工作流定义
	 */
	@RequestMapping(value = "workFlowConfigDelete.html", method = RequestMethod.GET, params = "workFlowConfigId")
	public String deleteWorkFlowConfig(@ModelAttribute("workFlowConfig") WorkFlowConfigInfo workFlowConfig) {
		workFlowConfigService.delete(workFlowConfig);
		return "redirect:workFlowConfigList.html?workFlowStatus=" + WorkFlowStatusEnum.SUSPEND.getValue();
	}

	/**
	 * 挂起工作流定义
	 */
	@RequestMapping(value = "workFlowConfigSuspend.html", method = RequestMethod.GET, params = "workFlowConfigId")
	public String suspendWorkFlowConfig(@ModelAttribute("workFlowConfig") WorkFlowConfigInfo workFlowConfig) {
		workFlowConfigService.suspendProcessDefinition(workFlowConfig);
		return "redirect:workFlowConfigList.html?workFlowStatus=" + WorkFlowStatusEnum.SUSPEND.getValue();
	}

	/**
	 * 激活工作流定义
	 */
	@RequestMapping(value = "workFlowConfigActive.html", method = RequestMethod.GET, params = "workFlowConfigId")
	public String activateWorkFlowConfig(@ModelAttribute("workFlowConfig") WorkFlowConfigInfo workFlowConfig) {
		workFlowConfigService.activateProcessDefinition(workFlowConfig);
		return "redirect:workFlowConfigList.html?workFlowStatus=" + WorkFlowStatusEnum.NORMAL.getValue();
	}

	/**
	 * 获取编辑工作流界面
	 */
	@RequestMapping(value = "workFlowConfigEdit.html", method = RequestMethod.GET)
	public String getWorkFlowConfigEditForm(@ModelAttribute("workFlowConfig") WorkFlowConfigInfo workFlowConfig,
			Model model) {
		model.addAttribute("workFlowConfig", workFlowConfig);
		if (!workFlowConfig.isIdPresented()) {
			model.addAttribute("workFlowTypes", ObjectUuidUtils.getSubclasses(WorkFlowBusinessModel.class));

			CompanyCriteria criteria = new CompanyCriteria();
			criteria.setStatus(CompanyStatusEnum.NORMAL);
			model.addAttribute("companies", companyService.getCollection(criteria));
		}
		return "admin/workFlowConfigEdit";
	}

	/**
	 * 处理编辑信息
	 */
	@RequestMapping(value = "workFlowConfigEdit.html", method = RequestMethod.POST)
	public String processWorkFlowConfigEditForm(@RequestParam MultipartFile uploadFile,
			@ModelAttribute("workFlowConfig") WorkFlowConfigInfo workFlowConfig, Model model) throws Exception {
		if (!uploadFile.isEmpty()) {
			workFlowConfigService.deployWorkFlow(workFlowConfig, uploadFile.getOriginalFilename(),
					uploadFile.getInputStream());
		}
		return "redirect:workFlowConfigList.html?workFlowStatus=" + workFlowConfig.getWorkFlowStatus().getValue();
	}

	/**
	 * 获取工作流程定义图
	 */
	@RequestMapping(value = "workFlowConfigList.html", method = RequestMethod.GET, params = "processDefinitionId")
	public String getProcessDefinitionDiagram(@RequestParam String processDefinitionId, HttpServletResponse response)
			throws Exception {
		Optional<InputStream> resourceAsStream = ActivitiHelper.getProcessDefinitionResource(processDefinitionId,
				ProcessResourceType.IMAGE);

		if (resourceAsStream.isPresent()) {
			byte[] b = new byte[1024];
			int len = -1;
			while ((len = resourceAsStream.get().read(b, 0, 1024)) != -1) {
				response.getOutputStream().write(b, 0, len);
			}
			return null;
		} else {
			return "redirect:workFlowConfigList.html";
		}
	}
}