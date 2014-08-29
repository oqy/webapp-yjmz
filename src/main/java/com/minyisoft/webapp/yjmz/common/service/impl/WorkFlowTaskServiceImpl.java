package com.minyisoft.webapp.yjmz.common.service.impl;

import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.minyisoft.webapp.core.model.criteria.PageDevice;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.security.SecurityUtils;
import com.minyisoft.webapp.yjmz.common.service.WorkFlowTaskService;

@Service("workFlowTaskService")
public class WorkFlowTaskServiceImpl implements WorkFlowTaskService {
	@Autowired
	private TaskService taskService;
	@Autowired
	private HistoryService historyService;

	@Override
	public long countTodoTasks(UserInfo user) {
		Assert.notNull(user, "没有指定待查询的用户信息");
		// 根据当前人的ID查询
		return taskService.createTaskQuery().taskCandidateOrAssigned(user.getCellPhoneNumber()).active().count();
	}

	@Override
	public List<Task> getTodoTasks(UserInfo user) {
		Assert.notNull(user, "没有指定待查询的用户信息");
		// 根据当前人的ID查询
		return taskService.createTaskQuery().taskCandidateOrAssigned(user.getCellPhoneNumber()).active()
				.orderByTaskCreateTime().desc().orderByTaskId().desc().list();
	}

	@Override
	public List<HistoricTaskInstance> getDoneTask(UserInfo user, PageDevice pageDevice) {
		Assert.notNull(user, "没有指定待查询的用户信息");
		HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
				.taskAssignee(user.getCellPhoneNumber()).finished();
		if (pageDevice != null) {
			pageDevice.setTotalRecords((int) query.count());
			return query.includeTaskLocalVariables().orderByHistoricTaskInstanceEndTime().desc()
					.listPage(pageDevice.getStartRowNumberOfCurrentPage() - 1, pageDevice.getRecordsPerPage());
		} else {
			return query.includeTaskLocalVariables().orderByHistoricTaskInstanceEndTime().desc().list();
		}
	}

	@Override
	public void completeTask(Task task, Map<String, Object> variables, Map<String, Object> variablesLocal) {
		Assert.notNull(task);
		// 若任务未签收，先进行任务签收操作
		HistoricTaskInstance historicTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId())
				.singleResult();
		if (historicTask == null || historicTask.getClaimTime() == null) {
			taskService.claim(task.getId(), SecurityUtils.getCurrentUser().getCellPhoneNumber());
		}
		taskService.setVariablesLocal(task.getId(), variablesLocal);
		taskService.complete(task.getId(), variables);
	}
}
