package com.minyisoft.webapp.yjmz.common.service;

import java.io.InputStream;

import com.google.common.base.Optional;
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
	 * 获取指定业务对象对应的工作流定义ID
	 * 
	 * @param businessModel
	 * @return
	 */
	Optional<String> getProcessDefinitionId(WorkFlowBusinessModel businessModel);
}
