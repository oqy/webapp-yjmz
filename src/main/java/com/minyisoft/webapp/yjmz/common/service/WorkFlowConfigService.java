package com.minyisoft.webapp.yjmz.common.service;

import java.io.InputStream;
import java.util.List;

import org.activiti.engine.history.HistoricProcessInstance;

import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.model.criteria.PageDevice;
import com.minyisoft.webapp.core.service.BaseService;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowConfigInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.WorkFlowConfigCriteria;

public interface WorkFlowConfigService extends BaseService<WorkFlowConfigInfo, WorkFlowConfigCriteria> {
	/**
	 * 发布工作流定义
	 * 
	 * @param config
	 * @param processDefinitionFileName
	 * @param processDefinitionFile
	 */
	void deployWorkFlow(WorkFlowConfigInfo config, String processDefinitionFileName, InputStream processDefinitionFile);

	/**
	 * 激活工作流定义
	 * 
	 * @param config
	 */
	void activateProcessDefinition(WorkFlowConfigInfo config);

	/**
	 * 挂起工作流定义
	 * 
	 * @param config
	 */
	void suspendProcessDefinition(WorkFlowConfigInfo config);

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
