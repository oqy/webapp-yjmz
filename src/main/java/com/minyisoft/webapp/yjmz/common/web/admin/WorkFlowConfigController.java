package com.minyisoft.webapp.yjmz.common.web.admin;

import java.io.InputStream;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.ProcessEngine;
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
import com.minyisoft.webapp.core.web.utils.SelectModuleFilter;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowConfigInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.WorkFlowConfigCriteria;
import com.minyisoft.webapp.yjmz.common.model.enumField.SupportedWorkFlowTypeEnum;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowStatusEnum;
import com.minyisoft.webapp.yjmz.common.security.SecurityUtils;
import com.minyisoft.webapp.yjmz.common.service.WorkFlowConfigService;
import com.minyisoft.webapp.yjmz.common.util.workflow.ActivitiHelper;
import com.minyisoft.webapp.yjmz.common.util.workflow.ActivitiHelper.ProcessResourceType;
import com.minyisoft.webapp.yjmz.common.web.ManageBaseController;

/**
 * @author qingyong_ou 工作流流程定义配置信息
 */
@Controller
@RequestMapping("/admin")
public class WorkFlowConfigController extends ManageBaseController {
	@Autowired
	private WorkFlowConfigService workFlowConfigService;
	private final ActivitiHelper activitiHelper;

	@Autowired
	public WorkFlowConfigController(ProcessEngine processEngine) {
		activitiHelper = ActivitiHelper.createInstance(processEngine);
	}

	/**
	 * 获取工作流流程定义配置信息
	 */
	@RequestMapping(value = "workFlowConfigList.do", method = RequestMethod.GET)
	public String getWorkFlowConfigList(WorkFlowConfigCriteria criteria, Model model) throws Exception {
		SecurityUtils.checkIsCurrentUserAdministrator();
		if (criteria.getPageDevice() == null) {
			criteria.setPageDevice(new PageDevice());
		}
		criteria.getPageDevice().setTotalRecords(workFlowConfigService.count(criteria));
		model.addAttribute("workFlowConfigs", workFlowConfigService.getCollection(criteria));

		SelectModuleFilter filter = new SelectModuleFilter(criteria);
		filter.addField("workFlowStatus", Arrays.asList(WorkFlowStatusEnum.values()));
		model.addAttribute("filter", filter);
		return "admin/system/workFlowConfigList";
	}

	/**
	 * 删除工作流定义
	 */
	@RequestMapping(value = "workFlowConfigList.do", method = RequestMethod.GET, params = "deleteId")
	public String deleteWorkFlowConfig(@RequestParam("deleteId") WorkFlowConfigInfo workFlowConfig) throws Exception {
		workFlowConfigService.delete(workFlowConfig);
		return "redirect:workFlowConfigList.do?workFlowStatus=" + WorkFlowStatusEnum.SUSPEND.getValue();
	}

	/**
	 * 挂起工作流定义
	 */
	@RequestMapping(value = "workFlowConfigList.do", method = RequestMethod.GET, params = "suspendId")
	public String suspendWorkFlowConfig(@RequestParam("suspendId") WorkFlowConfigInfo workFlowConfig) throws Exception {
		workFlowConfigService.suspendProcessDefinition(workFlowConfig);
		return "redirect:workFlowConfigList.do?workFlowStatus=" + WorkFlowStatusEnum.SUSPEND.getValue();
	}

	/**
	 * 激活工作流定义
	 */
	@RequestMapping(value = "workFlowConfigList.do", method = RequestMethod.GET, params = "activateId")
	public String activateWorkFlowConfig(@RequestParam("activateId") WorkFlowConfigInfo workFlowConfig)
			throws Exception {
		workFlowConfigService.activateProcessDefinition(workFlowConfig);
		return "redirect:workFlowConfigList.do?workFlowStatus=" + WorkFlowStatusEnum.NORMAL.getValue();
	}

	/**
	 * 获取编辑工作流界面
	 */
	@RequestMapping(value = "workFlowConfigEdit.do", method = RequestMethod.GET)
	public String getWorkFlowConfigEditForm(@RequestParam(required = false) WorkFlowConfigInfo workFlowConfig,
			Model model) throws Exception {
		model.addAttribute("workFlowConfig", workFlowConfig);
		if (workFlowConfig == null) {
			model.addAttribute("workFlowTypes", SupportedWorkFlowTypeEnum.values());
		}
		return "admin/system/workFlowConfigEdit";
	}

	@ModelAttribute
	public WorkFlowConfigInfo getExistWorkFlowConfig(
			@RequestParam(value = "id", required = false) WorkFlowConfigInfo config) {
		return config != null ? config : null;
	}

	/**
	 * 处理编辑信息
	 */
	@RequestMapping(value = "workFlowConfigEdit.do", method = RequestMethod.POST)
	public String processWorkFlowConfigEditForm(@RequestParam MultipartFile uploadFile,
			@ModelAttribute("id") WorkFlowConfigInfo workFlowConfig, Model model) throws Exception {
		if (!uploadFile.isEmpty()) {
			workFlowConfig.setWorkFlowStatus(WorkFlowStatusEnum.NORMAL);
			workFlowConfigService.deployWorkFlow(workFlowConfig, uploadFile.getOriginalFilename(),
					uploadFile.getInputStream());
		}
		return "redirect:workFlowConfigList.do?workFlowStatus=" + workFlowConfig.getWorkFlowStatus().getValue();
	}

	/**
	 * 获取工作流程定义图
	 */
	@RequestMapping(value = "workFlowConfigList.do", method = RequestMethod.GET, params = "processDefinitionId")
	public String getProcessDefinitionDiagram(@RequestParam String processDefinitionId, HttpServletResponse response)
			throws Exception {
		Optional<InputStream> resourceAsStream = activitiHelper.getProcessDefinitionResource(processDefinitionId,
				ProcessResourceType.IMAGE);

		if (resourceAsStream.isPresent()) {
			byte[] b = new byte[1024];
			int len = -1;
			while ((len = resourceAsStream.get().read(b, 0, 1024)) != -1) {
				response.getOutputStream().write(b, 0, len);
			}
			return null;
		} else {
			return "redirect:workFlowConfigList.do";
		}
	}

	/**
	 * 获取工作流程实例列表
	 */
	@RequestMapping(value = "processInstanceList.do", method = RequestMethod.GET, params = "workFlowConfigId")
	public String getProcessInstanceList(@RequestParam("workFlowConfigId") WorkFlowConfigInfo workFlowConfig,
			PageDevice pageDevice, Model model) throws Exception {
		model.addAttribute("workFlowConfig", workFlowConfig);
		model.addAttribute("processInstances",
				workFlowConfigService.getProcessInstances(workFlowConfig.getProcessDefinitionId(), pageDevice));
		model.addAttribute("pageDevice", pageDevice);
		return "admin/system/processInstanceList";
	}
}