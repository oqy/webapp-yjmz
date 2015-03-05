package com.minyisoft.webapp.yjmz.common.service;

import java.util.List;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;

import com.minyisoft.webapp.core.model.criteria.PageDevice;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;

public interface WorkFlowProcessService {
	/**
	 * 启动工作流流程
	 * 
	 * @param owner
	 * @param businessModel
	 * @return 若存在匹配流程且成功启动，返回流程id
	 */
	void startProcess(WorkFlowBusinessModel businessModel);

	/**
	 * 删除运行中的工作流实例
	 * 
	 * @param processInstanceId
	 * @param deleteReason
	 */
	void deleteRunningProcess(String processInstanceId, String deleteReason);

	/**
	 * 获取指定流程定义的运行流程实例
	 * 
	 * @param processDefinitionId
	 * @param pageDevice
	 * @return
	 */
	List<ProcessInstance> getProcessInstances(String processDefinitionId, PageDevice pageDevice);

	/**
	 * 获取指定流程发起人的流程实例数
	 * 
	 * @param processInitiator
	 * @return
	 */
	long countProcessInstances(UserInfo processInitiator);

	/**
	 * 获取指定流程发起人的流程实例
	 * 
	 * @param processInitiator
	 * @return
	 */
	List<ProcessInstance> getProcessInstances(UserInfo processInitiator);

	/**
	 * 获取指定流程定义的历史流程实例
	 * 
	 * @param processDefinitionId
	 * @param pageDevice
	 * @return
	 */
	List<HistoricProcessInstance> getHistoricProcessInstances(String processDefinitionId, PageDevice pageDevice);
}
