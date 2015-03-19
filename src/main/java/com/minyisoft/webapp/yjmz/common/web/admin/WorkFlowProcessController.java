package com.minyisoft.webapp.yjmz.common.web.admin;

import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Optional;
import com.minyisoft.webapp.core.model.criteria.PageDevice;
import com.minyisoft.webapp.core.web.BaseController;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowConfigInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.UserOrgRelationCriteria;
import com.minyisoft.webapp.yjmz.common.model.entity.WeixinTemplateMessage;
import com.minyisoft.webapp.yjmz.common.security.SecurityUtils;
import com.minyisoft.webapp.yjmz.common.service.MessageService;
import com.minyisoft.webapp.yjmz.common.service.UserOrgRelationService;
import com.minyisoft.webapp.yjmz.common.service.WorkFlowProcessService;
import com.minyisoft.webapp.yjmz.common.service.WorkFlowTaskService;
import com.minyisoft.webapp.yjmz.common.util.workflow.ActivitiHelper;
import com.minyisoft.webapp.yjmz.oa.model.MaintainReqBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.enumField.MaintainTypeEnum;

/**
 * @author qingyong_ou 工作流流程实例信息
 */
@Controller
@RequestMapping("/admin")
public class WorkFlowProcessController extends BaseController {
	@Autowired
	private WorkFlowProcessService workFlowProcessService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private UserOrgRelationService userOrgRelationService;
	@Autowired
	private WorkFlowTaskService workFlowTaskService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private MessageService messageService;
	@Value("#{applicationProperties['web.domain']}")
	private String webDomain;

	/**
	 * 获取运行中的工作流程实例列表
	 */
	@RequestMapping(value = "processInstances.html", method = RequestMethod.GET, params = "workFlowConfigId")
	public String getProcessInstances(@RequestParam("workFlowConfigId") WorkFlowConfigInfo workFlowConfig,
			PageDevice pageDevice, Model model) {
		model.addAttribute("workFlowConfig", workFlowConfig);
		model.addAttribute("processInstances",
				workFlowProcessService.getProcessInstances(workFlowConfig.getProcessDefinitionId(), pageDevice));
		model.addAttribute("pageDevice", pageDevice);
		return "admin/processInstances";
	}

	/**
	 * 挂起工作流程实例
	 */
	@RequestMapping(value = "processInstances.do", method = RequestMethod.GET, params = "suspendId")
	@ResponseBody
	public boolean suspendProcessInstance(@RequestParam String suspendId) {
		try {
			SecurityUtils.checkIsCurrentUserAdministrator();
			runtimeService.suspendProcessInstanceById(suspendId);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}

	/**
	 * 激活工作流程实例
	 */
	@RequestMapping(value = "processInstances.do", method = RequestMethod.GET, params = "activeId")
	@ResponseBody
	public boolean activeProcessInstance(@RequestParam String activeId) {
		try {
			SecurityUtils.checkIsCurrentUserAdministrator();
			runtimeService.activateProcessInstanceById(activeId);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}

	/**
	 * 删除流程实例
	 */
	@RequestMapping(value = "processInstances.html", method = RequestMethod.GET, params = { "workFlowConfigId",
			"deleteId" })
	public String getProcessInstances(@RequestParam("workFlowConfigId") String workFlowConfigId,
			@RequestParam("deleteId") String deleteId,
			@RequestParam(value = "deleteReason", required = false) String deleteReason) {
		workFlowProcessService.deleteRunningProcess(deleteId, deleteReason);
		return "redirect:processInstances.html?workFlowConfigId=" + workFlowConfigId;
	}

	/**
	 * 获取已完成工作流程实例列表
	 */
	@RequestMapping(value = "historicProcessInstances.html", method = RequestMethod.GET, params = "workFlowConfigId")
	public String getHistoricProcessInstances(@RequestParam("workFlowConfigId") WorkFlowConfigInfo workFlowConfig,
			PageDevice pageDevice, Model model) {
		model.addAttribute("workFlowConfig", workFlowConfig);
		model.addAttribute("processInstances",
				workFlowProcessService.getHistoricProcessInstances(workFlowConfig.getProcessDefinitionId(), pageDevice));
		model.addAttribute("pageDevice", pageDevice);
		return "admin/historicProcessInstances";
	}

	/**
	 * 查看单据详情
	 */
	@RequestMapping(value = "workFlowDetail.html", method = RequestMethod.GET, params = "workFlowModelId")
	public String viewWorkFlowDetail(
			@RequestParam(value = "workFlowModelId", required = false) WorkFlowBusinessModel businessModel, Model model) {
		model.addAttribute("businessModel", businessModel);
		if (businessModel instanceof MaintainReqBillInfo) {
			model.addAttribute("maintainTypes", MaintainTypeEnum.values());
		}
		if (!businessModel.isProcessUnStarted()) {
			HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
					.processInstanceId(businessModel.getProcessInstanceId()).singleResult();
			model.addAttribute("processInstance", processInstance);
			model.addAttribute("historicTaskInstances",
					ActivitiHelper.getHistoricTaskInstances(businessModel.getProcessInstanceId()));

			UserOrgRelationCriteria criteria = new UserOrgRelationCriteria();
			criteria.setOrg(businessModel.getCompany());
			model.addAttribute("userOrgRelations", userOrgRelationService.getCollection(criteria));
		}
		return "admin/workFlowDetail";
	}

	/**
	 * 更改任务执行人
	 */
	@RequestMapping(value = "changeTaskAssignee.html", method = RequestMethod.POST, params = "workFlowModelId")
	public String changeTaskAssignee(
			@RequestParam(value = "workFlowModelId", required = false) WorkFlowBusinessModel businessModel,
			@RequestParam(value = "taskId", required = false) String taskId,
			@RequestParam(value = "assignee", required = false) UserInfo assignee) {
		if (businessModel != null && assignee != null) {
			workFlowTaskService.changeAssignee(taskId, assignee);

			Task task = null;
			if (StringUtils.isNotBlank(assignee.getWeixinOpenId())
					&& (task = taskService.createTaskQuery().taskId(taskId).singleResult()) != null) {
				// 微信模板消息待发送内容
				String[] weixinTemplateMessages = new String[6];
				weixinTemplateMessages[0] = "您好，您有一条新的待处理工作流任务\n";
				weixinTemplateMessages[1] = businessModel.getProcessInstanceName();
				weixinTemplateMessages[2] = task.getName();
				weixinTemplateMessages[3] = businessModel.getCreateUser().getName();
				weixinTemplateMessages[4] = DateFormatUtils.format(task.getCreateTime(), "yyyy年M月d日HH时mm分");
				weixinTemplateMessages[5] = "\n请您及时处理";

				messageService.sendWeixinTemplateMessage(assignee.getWeixinOpenId(),
						WeixinTemplateMessage.PENDING_TRANSACTION_NOTIFY, webDomain + "/viewDetail.html?billId="
								+ businessModel.getId(), weixinTemplateMessages);
			}

		}

		if (businessModel != null) {
			return "redirect:workFlowDetail.html?workFlowModelId=" + businessModel.getId();
		}
		return "redirect:processInstances.html";
	}

	/**
	 * 获取流程实例运行图
	 */
	@RequestMapping(value = "processInstanceDiagram.html", method = RequestMethod.GET, params = "processInstanceId")
	public void getProcessInstanceDiagram(@RequestParam String processInstanceId, HttpServletResponse response)
			throws Exception {
		Optional<InputStream> imageStream = ActivitiHelper.getProcessInstanceDiagram(processInstanceId);
		if (imageStream.isPresent()) {
			byte[] b = new byte[1024];
			int len;
			while ((len = imageStream.get().read(b, 0, 1024)) != -1) {
				response.getOutputStream().write(b, 0, len);
			}
		}
	}
}