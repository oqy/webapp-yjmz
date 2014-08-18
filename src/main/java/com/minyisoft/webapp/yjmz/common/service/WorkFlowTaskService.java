package com.minyisoft.webapp.yjmz.common.service;

import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;

import com.minyisoft.webapp.core.model.criteria.PageDevice;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;

public interface WorkFlowTaskService {
	/**
	 * 获取待处理任务列表
	 * 
	 * @param user
	 * @return
	 */
	List<Task> getTodoTasks(UserInfo user);

	/**
	 * 获取已处理任务列表
	 * 
	 * @param user
	 * @param pageDevice
	 * @return
	 */
	List<HistoricTaskInstance> getDoneTask(UserInfo user, PageDevice pageDevice);

	/**
	 * 完成任务
	 * 
	 * @param task
	 * @param variables
	 *            流程变量
	 * @param variablesLocal
	 *            任务本地变量
	 */
	void completeTask(Task task, Map<String, Object> variables, Map<String, Object> variablesLocal);

	/**
	 * 获取用户参与处理的所有流程实例
	 * 
	 * @param user
	 * @param pageDevice
	 * @return
	 */
	List<HistoricProcessInstance> getInvolvedProcessInstance(UserInfo user, PageDevice pageDevice);
}
