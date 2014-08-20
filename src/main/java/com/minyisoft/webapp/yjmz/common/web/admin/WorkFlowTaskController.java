package com.minyisoft.webapp.yjmz.common.web.admin;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.FormService;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minyisoft.webapp.core.model.criteria.PageDevice;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.core.web.BaseController;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.UserCriteria;
import com.minyisoft.webapp.yjmz.common.service.UserService;
import com.minyisoft.webapp.yjmz.common.service.WorkFlowTaskService;
import com.minyisoft.webapp.yjmz.common.util.workflow.ActivitiHelper;
import com.minyisoft.webapp.yjmz.common.util.workflow.UserFormType;

/**
 * @author qingyong_ou 工作流任务中心controller
 */
@Controller
@RequestMapping("/admin")
public class WorkFlowTaskController extends BaseController {
	@Autowired
	private WorkFlowTaskService workFlowTaskService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private FormService formService;
	@Autowired
	private UserService userService;

	/**
	 * 获取我参与的主任务列表
	 */
	@RequestMapping(value = "myInvolvedProcessInstances.do", method = RequestMethod.GET)
	public String getMyInvolvedProcessInstances(@ModelAttribute("currentUser") UserInfo currentUser, Model model,
			PageDevice pageDevice) {
		model.addAttribute("involvedProcessInstances",
				workFlowTaskService.getInvolvedProcessInstance(currentUser, pageDevice));
		model.addAttribute("pageDevice", pageDevice);
		return "admin/common/myInvolvedProcessInstances";
	}

	/**
	 * 获取我的待处理任务列表
	 */
	@RequestMapping(value = "myTodoTasks.do", method = RequestMethod.GET)
	public String getMyTaskList(@ModelAttribute("currentUser") UserInfo currentUser, Model model) {
		model.addAttribute("todoTasks", workFlowTaskService.getTodoTasks(currentUser));
		return "admin/common/myTodoTasks";
	}

	/**
	 * 查看待处理任务详细信息
	 */
	@RequestMapping(value = "processTodoTask.do", method = RequestMethod.GET, params = "taskId")
	public String viewTodoTask(@ModelAttribute("currentUser") UserInfo currentUser, @RequestParam String taskId,
			Model model) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		model.addAttribute("task", task);
		List<FormProperty> formProperties = formService.getTaskFormData(task.getId()).getFormProperties();
		for (FormProperty property : formProperties) {
			if (property.getType() instanceof UserFormType && !model.containsAttribute("optionUsers")) {
				List<UserInfo> optionUsers = Lists.newArrayList();
				optionUsers.add(currentUser);
				UserCriteria criteria = new UserCriteria();
				// criteria.setOrg(DefaultOrgEnum.ADMIN_ORG);
				// criteria.setUpperUser(currentUser);
				optionUsers.addAll(userService.getCollection(criteria));
				model.addAttribute("optionUsers", optionUsers);
			}
		}
		model.addAttribute("formProperties", formProperties);
		_prepareProcessInstanceDetailData(task.getProcessInstanceId(), model);
		return "admin/common/myTodoTaskProcess";
	}

	/**
	 * 签收任务
	 */
	@RequestMapping(value = "claimTodoTask.do", method = RequestMethod.GET, params = "taskId")
	public String claimTodoTask(@ModelAttribute("currentUser") UserInfo currentUser, @RequestParam String taskId) {
		taskService.claim(taskId, currentUser.getId());
		return "redirect:myTodoTasks.do";
	}

	/**
	 * 处理任务
	 */
	@RequestMapping(value = "processTodoTask.do", method = RequestMethod.POST, params = "taskId")
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
		return "redirect:myTodoTasks.do";
	}

	/**
	 * 获取我的已处理任务列表
	 */
	@RequestMapping(value = "myDoneTasks.do", method = RequestMethod.GET)
	public String getMyDoneTaskList(@ModelAttribute("currentUser") UserInfo currentUser, Model model,
			PageDevice pageDevice) {
		model.addAttribute("doneTasks", workFlowTaskService.getDoneTask(currentUser, pageDevice));
		model.addAttribute("pageDevice", pageDevice);
		return "admin/common/myDoneTasks";
	}

	/**
	 * 获取流程实例运行图
	 */
	@RequestMapping(value = "processInstanceDiagram.do", method = RequestMethod.GET, params = "processInstanceId")
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
	 * 获取流程实例详细信息
	 */
	@RequestMapping(value = "processInstanceDetail.do", method = RequestMethod.GET, params = "processInstanceId")
	public String getProcessInstanceDetail(@RequestParam String processInstanceId, Model model) {
		_prepareProcessInstanceDetailData(processInstanceId, model);
		return "admin/common/processInstanceDetail";
	}

	/**
	 * 组装查看流程实例详细信息所需数据
	 */
	private void _prepareProcessInstanceDetailData(String processInstanceId, Model model) {
		model.addAttribute("processInstanceId", processInstanceId);
		Optional<String> businessKey = ActivitiHelper.getProcessInstanceBusinessKey(processInstanceId);
		if (businessKey.isPresent() && ObjectUuidUtils.isLegalId(businessKey.get())) {
			model.addAttribute("businessKey", businessKey.get());
		}
		model.addAttribute("historicTaskInstances", ActivitiHelper.getHistoricTaskInstances(processInstanceId));
	}
}
