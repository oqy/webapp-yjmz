package com.minyisoft.webapp.yjmz.common.service;

import java.util.List;

import org.activiti.engine.history.HistoricProcessInstance;

import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.model.criteria.PageDevice;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;

public interface WorkFlowProcessService {
	/**
	 * 启动工作流流程
	 * 
	 * @param owner
	 * @param businessModel
	 * @return 若存在匹配流程且成功启动，返回流程id
	 */
	void startProcess(ISystemOrgObject owner, WorkFlowBusinessModel businessModel);

	/**
	 * 删除运行中的工作流实例
	 * 
	 * @param processInstanceId
	 */
	void deleteRunningProcess(String processInstanceId);

	/**
	 * 获取指定流程定义的流程实例
	 * 
	 * @param processDefinitionId
	 * @param pageDevice
	 * @return
	 */
	List<HistoricProcessInstance> getProcessInstances(String processDefinitionId, PageDevice pageDevice);
}
