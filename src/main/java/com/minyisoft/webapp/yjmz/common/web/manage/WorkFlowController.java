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
import org.activiti.engine.impl.form.BooleanFormType;
import org.activiti.engine.impl.form.DateFormType;
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
import com.minyisoft.webapp.yjmz.common.model.CompanyInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;
import com.minyisoft.webapp.yjmz.common.service.UserService;
import com.minyisoft.webapp.yjmz.common.service.WorkFlowConfigService;
import com.minyisoft.webapp.yjmz.common.service.WorkFlowTaskService;
import com.minyisoft.webapp.yjmz.common.util.workflow.ActivitiHelper;
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
	private WorkFlowConfigService workFlowConfigService;
	@Autowired
	private WorkFlowTaskService workFlowTaskService;
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
	public String startWorkFlow(@ModelAttribute("currentCompany") CompanyInfo currentCompany,
			@RequestParam(value = "workFlowModelId", required = false) WorkFlowBusinessModel businessModel) {
		workFlowConfigService.startProcess(currentCompany, businessModel);
		return "redirect:workFlowDetail.html?workFlowModelId=" + businessModel.getId();
	}

	/**
	 * 查看单据详情
	 */
	@RequestMapping(value = "workFlowDetail.html", method = RequestMethod.GET, params = "workFlowModelId")
	public String viewWorkFlowDetail(@ModelAttribute("currentUser") UserInfo currentUser,
			@RequestParam(value = "workFlowModelId", required = false) WorkFlowBusinessModel businessModel, Model model) {
		model.addAttribute("businessModel", businessModel);
		if (businessModel instanceof MaintainReqBillInfo) {
			model.addAttribute("maintainTypes", MaintainTypeEnum.values());
		}
		if (!businessModel.isProcessUnStarted()) {
			model.addAttribute("processInstance", historyService.createHistoricProcessInstanceQuery()
					.processInstanceId(businessModel.getProcessInstanceId()).singleResult());
			model.addAttribute("historicTaskInstances",
					ActivitiHelper.getHistoricTaskInstances(businessModel.getProcessInstanceId()));

			List<Task> activeTasks = taskService.createTaskQuery()
					.processInstanceId(businessModel.getProcessInstanceId())
					.taskCandidateOrAssigned(currentUser.getCellPhoneNumber()).active().list();
			if (!activeTasks.isEmpty()) {
				Task task = activeTasks.get(0);
				model.addAttribute("task", task);
				/*
				 * List<FormProperty> formProperties =
				 * formService.getTaskFormData
				 * (task.getId()).getFormProperties(); for (FormProperty
				 * property : formProperties) { if (property.getType()
				 * instanceof UserFormType &&
				 * !model.containsAttribute("optionUsers")) { List<UserInfo>
				 * optionUsers = Lists.newArrayList();
				 * optionUsers.add(currentUser); UserCriteria criteria = new
				 * UserCriteria(); criteria.setOrg(DefaultOrgEnum.ADMIN_ORG);
				 * criteria.setUpperUser(currentUser);
				 * optionUsers.addAll(userService.getCollection(criteria));
				 * model.addAttribute("optionUsers", optionUsers); } }
				 */
				model.addAttribute("formProperties", formService.getTaskFormData(task.getId()).getFormProperties());
			}
		}
		return "manage/workFlowDetail";
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

	/**
	 * 获取我的已处理任务列表
	 */
	@RequestMapping(value = "myDoneTasks.html", method = RequestMethod.GET)
	public String getMyDoneTaskList(@ModelAttribute("currentUser") UserInfo currentUser, Model model,
			PageDevice pageDevice) {
		model.addAttribute("doneTasks", workFlowTaskService.getDoneTask(currentUser, pageDevice));
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
	 * 签收任务
	 */
	@RequestMapping(value = "claimTodoTask.html", method = RequestMethod.GET, params = "taskId")
	public String claimTodoTask(@ModelAttribute("currentUser") UserInfo currentUser, @RequestParam String taskId) {
		taskService.claim(taskId, currentUser.getCellPhoneNumber());
		return "redirect:myTodoTasks.html";
	}

	/**
	 * 处理任务
	 */
	@RequestMapping(value = "processTodoTask.html", method = RequestMethod.POST, params = "taskId")
	public String processTodoTask(@RequestParam String taskId, HttpServletRequest request,
			@RequestParam(required = false) String description) throws Exception {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		Map<String, Object> variables = Maps.newLinkedHashMap();
		Map<String, Object> variablesLocal = Maps.newLinkedHashMap();
		for (FormProperty property : formService.getTaskFormData(task.getId()).getFormProperties()) {
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
				} else if (property.getType() instanceof DateFormType) {
					variables.put(property.getId(), DateUtils.parseDate(propertyVal, new String[] { "yyyy-MM-dd" }));
					variablesLocal.put(property.getName(), propertyVal);
				}
			}
		}
		if (StringUtils.isNotBlank(description)) {
			variablesLocal.put("处理备注", description);
		}
		workFlowTaskService.completeTask(task, variables, variablesLocal);
		return "redirect:myTodoTasks.html";
	}
}
