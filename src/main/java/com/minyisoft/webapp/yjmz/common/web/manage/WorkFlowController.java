package com.minyisoft.webapp.yjmz.common.web.manage;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.form.BooleanFormType;
import org.activiti.engine.impl.form.DateFormType;
import org.activiti.engine.impl.form.LongFormType;
import org.activiti.engine.impl.form.StringFormType;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.minyisoft.webapp.core.model.criteria.PageDevice;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;
import com.minyisoft.webapp.yjmz.common.service.UserService;
import com.minyisoft.webapp.yjmz.common.service.WorkFlowConfigService;
import com.minyisoft.webapp.yjmz.common.service.WorkFlowProcessService;
import com.minyisoft.webapp.yjmz.common.service.WorkFlowTaskService;
import com.minyisoft.webapp.yjmz.common.util.workflow.ActivitiHelper;
import com.minyisoft.webapp.yjmz.common.util.workflow.ActivitiHelper.ProcessResourceType;
import com.minyisoft.webapp.yjmz.common.util.workflow.UserFormType;
import com.minyisoft.webapp.yjmz.oa.model.MaintainReqBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.enumField.MaintainTypeEnum;

/**
 * @author qingyong_ou 工作流任务中心controller
 */
@Controller
@RequestMapping("/manage")
public class WorkFlowController extends ManageBaseController {
	@Autowired
	private WorkFlowProcessService workFlowProcessService;
	@Autowired
	private WorkFlowTaskService workFlowTaskService;
	@Autowired
	private WorkFlowConfigService workFlowConfigService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private FormService formService;
	@Autowired
	private UserService userService;
	@Autowired
	private HistoryService historyService;

	/**
	 * 启动工作流
	 */
	@RequestMapping(value = "startWorkFlow.html", method = RequestMethod.GET, params = "workFlowModelId")
	public String startWorkFlow(
			@RequestParam(value = "workFlowModelId", required = false) WorkFlowBusinessModel businessModel) {
		workFlowProcessService.startProcess(businessModel);
		return "redirect:workFlowDetail.html?workFlowModelId=" + businessModel.getId();
	}

	/**
	 * 查看单据详情
	 */
	@RequestMapping(value = "workFlowDetail.html", method = RequestMethod.GET, params = "workFlowModelId")
	public String viewWorkFlowDetail(@ModelAttribute("currentUser") UserInfo currentUser,
			@RequestParam(value = "workFlowModelId", required = false) WorkFlowBusinessModel businessModel, Model model) {
		if (businessModel == null) {
			return "redirect:myTodoTasks.html";
		}

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

			List<Task> activeTasks = taskService.createTaskQuery()
					.processInstanceId(businessModel.getProcessInstanceId())
					.taskCandidateOrAssigned(currentUser.getCellPhoneNumber()).active().list();
			if (!activeTasks.isEmpty()) {
				TaskFormData taskFormData = formService.getTaskFormData(activeTasks.get(0).getId());
				if (StringUtils.isNotBlank(taskFormData.getFormKey())) {
					model.addAttribute(businessModel.getBusinessModelProcessVariableName(), businessModel);
				}
				model.addAttribute("taskFormData", taskFormData);
			}
		} else {
			model.addAttribute("processDefinitionId", workFlowConfigService.getProcessDefinitionId(businessModel)
					.orNull());
		}
		return "manage/workFlowDetail";
	}

	/**
	 * 获取流程运行图
	 */
	@RequestMapping(value = "processInstanceDiagram.html", method = RequestMethod.GET)
	public void getProcessInstanceDiagram(
			@RequestParam(value = "processInstanceId", required = false) String processInstanceId,
			@RequestParam(value = "processDefinitionId", required = false) String processDefinitionId,
			HttpServletResponse response) throws Exception {
		Optional<InputStream> imageStream = Optional.absent();
		if (StringUtils.isNotBlank(processDefinitionId)) {
			imageStream = ActivitiHelper.getProcessDefinitionResource(processDefinitionId, ProcessResourceType.IMAGE);
		} else if (StringUtils.isNotBlank(processInstanceId)) {
			imageStream = ActivitiHelper.getProcessInstanceDiagram(processInstanceId);
		}
		if (imageStream.isPresent()) {
			byte[] b = new byte[1024];
			int len;
			while ((len = imageStream.get().read(b, 0, 1024)) != -1) {
				response.getOutputStream().write(b, 0, len);
			}
		}
	}

	/**
	 * 获取我的已处理任务列表
	 */
	@RequestMapping(value = "myDoneTasks.html", method = RequestMethod.GET)
	public String getMyDoneTaskList(@ModelAttribute("currentUser") UserInfo currentUser, Model model,
			PageDevice pageDevice) {
		model.addAttribute("doneTasks", workFlowTaskService.getDoneTasks(currentUser, pageDevice));
		model.addAttribute("pageDevice", pageDevice);
		return "manage/myDoneTasks";
	}

	/**
	 * 获取我的待处理任务列表
	 */
	@RequestMapping(value = "myTodoTasks.html", method = RequestMethod.GET)
	public String getMyTaskList(@ModelAttribute("currentUser") UserInfo currentUser, Model model) {
		model.addAttribute("todoTasks", workFlowTaskService.getTodoTasks(currentUser));
		return "manage/myTodoTasks";
	}

	/**
	 * 获取由我发起正在审批的流程列表
	 */
	@RequestMapping(value = "myRunningProcesses.html", method = RequestMethod.GET)
	public String getMyRunningProcesses(@ModelAttribute("currentUser") UserInfo currentUser, Model model) {
		model.addAttribute("myRunningProcesses", workFlowProcessService.getProcessInstances(currentUser));
		return "manage/myRunningProcesses";
	}

	/**
	 * 签收任务
	 */
	@RequestMapping(value = "claimTodoTask.html", method = RequestMethod.GET, params = "taskId")
	public String claimTodoTask(@ModelAttribute("currentUser") UserInfo currentUser, @RequestParam String taskId) {
		taskService.claim(taskId, currentUser.getCellPhoneNumber());
		return "redirect:myTodoTasks.html";
	}

	@ModelAttribute("workFlowBusinessModel")
	protected WorkFlowBusinessModel populateBusinessModel(
			@RequestParam(value = "workFlowBusinessModelId", required = false) WorkFlowBusinessModel workFlowBusinessModel) {
		return workFlowBusinessModel;
	}

	/**
	 * 处理任务
	 */
	@RequestMapping(value = "processTodoTask.html", method = RequestMethod.POST, params = "taskId")
	public String processTodoTask(@ModelAttribute("workFlowBusinessModel") WorkFlowBusinessModel workFlowBusinessModel,
			@RequestParam String taskId, HttpServletRequest request, @RequestParam(required = false) String description)
			throws Exception {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			return "redirect:myTodoTasks.html";
		}
		TaskFormData taskFormData = formService.getTaskFormData(task.getId());

		// 根据formProperties获取对应值
		Map<String, Object> variables = Maps.newLinkedHashMap(), variablesLocal = Maps.newLinkedHashMap();
		for (FormProperty property : taskFormData.getFormProperties()) {
			String propertyVal = request.getParameter(property.getId());
			if (StringUtils.isNotBlank(propertyVal)) {
				if (property.getType() instanceof BooleanFormType) {
					variables.put(property.getId(), Boolean.parseBoolean(propertyVal));
					variablesLocal.put(property.getName(), Boolean.parseBoolean(propertyVal) ? "是" : "否");
				} else if (property.getType() instanceof UserFormType) {
					UserInfo user = userService.getValue(propertyVal);
					variables.put(property.getId(), user);
					if (user != null) {
						variablesLocal.put(property.getName(), user.getName());
					}
				} else if (property.getType() instanceof LongFormType) {
					variables.put(property.getId(), Long.parseLong(propertyVal));
				} else if (property.getType() instanceof DateFormType) {
					variables.put(property.getId(), DateUtils.parseDate(propertyVal, new String[] { "yyyy-MM-dd" }));
					variablesLocal.put(property.getName(), propertyVal);
				} else if (property.getType() instanceof StringFormType) {
					variables.put(property.getId(), propertyVal);
					variablesLocal.put(property.getName(), propertyVal);
				}
			}
		}

		// 无fromKey时，无需对业务对象进行更新操作
		if (StringUtils.isBlank(taskFormData.getFormKey())) {
			if (StringUtils.isNotBlank(description)) {
				variables.put("description", description);
				variablesLocal.put("处理备注", description);
			}
			workFlowTaskService.completeTask(task, variables, variablesLocal);
		}
		// 根据fromKey获取的对应值更新业务对象
		else {
			workFlowTaskService.completeTask(task, variables, variablesLocal, workFlowBusinessModel);
		}
		return "redirect:myTodoTasks.html";
	}
}
