package com.minyisoft.webapp.yjmz.common.util.workflow;

import java.io.InputStream;
import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.ProcessEngineImpl;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.ReadOnlyProcessDefinition;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.util.Assert;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;

/**
 * @author qingyong_ou Activiti工具类
 */
public final class ActivitiHelper {
	private static final ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	private ActivitiHelper() {
	}

	/**
	 * @author qingyong_ou 工作流资源类型枚举
	 * 
	 */
	public enum ProcessResourceType {
		XML, IMAGE;
	}

	/**
	 * 获取流程定义资源
	 * 
	 * @param processDefinitionId
	 * @param resourceType
	 * @return
	 */
	public static Optional<InputStream> getProcessDefinitionResource(String processDefinitionId,
			ProcessResourceType resourceType) {
		Assert.hasText(processDefinitionId, "工作流程定义ID不能为空");
		ProcessDefinition processDefinition = processEngine.getRepositoryService().createProcessDefinitionQuery()
				.processDefinitionId(processDefinitionId).singleResult();
		if (processDefinition != null && resourceType == ProcessResourceType.XML) {
			return Optional.of(processEngine.getRepositoryService().getProcessModel(processDefinition.getId()));
		} else if (processDefinition != null && resourceType == ProcessResourceType.IMAGE) {
			return Optional.of(processEngine.getRepositoryService().getProcessDiagram(processDefinition.getId()));
		} else {
			return Optional.absent();
		}
	}

	/**
	 * 获取指定流程实例ID对应的businessKey
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public static Optional<String> getProcessInstanceBusinessKey(String processInstanceId) {
		Assert.hasText(processInstanceId, "流程实例ID不能为空");
		// 首先判断是否为正在运行的实例
		ProcessInstance processInstance = processEngine.getRuntimeService().createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		if (processInstance != null) {
			return Optional.of(processInstance.getBusinessKey());
		}
		// 然后判断是否为已结束的实例
		HistoricProcessInstance historicProcessInstance = processEngine.getHistoryService()
				.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		if (historicProcessInstance != null) {
			return Optional.of(historicProcessInstance.getBusinessKey());
		}
		return Optional.absent();
	}

	/**
	 * 获取指定流程实例的流程图
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public static Optional<InputStream> getProcessInstanceDiagram(String processInstanceId) {
		Assert.hasText(processInstanceId, "流程实例ID不能为空");
		// 首先判断是否为正在运行的实例
		ProcessInstance processInstance = processEngine.getRuntimeService().createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		if (processInstance != null) {
			return Optional.of(getProcessInstanceDiagram(processInstance.getId(),
					processInstance.getProcessDefinitionId(), true));
		}
		// 然后判断是否为已结束的实例
		HistoricProcessInstance historicProcessInstance = processEngine.getHistoryService()
				.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		if (historicProcessInstance != null) {
			return Optional.of(getProcessInstanceDiagram(historicProcessInstance.getId(),
					historicProcessInstance.getProcessDefinitionId(), false));
		}
		return Optional.absent();
	}

	private static InputStream getProcessInstanceDiagram(String processInstanceId, String processDefinitionId,
			boolean isRuntimeProcessInstance) {
		List<String> highLightedActivities = Lists.newArrayList();
		List<HistoricActivityInstance> historicActivityInstances = processEngine.getHistoryService()
				.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
				.orderByHistoricActivityInstanceStartTime().asc().list();
		for (HistoricActivityInstance hai : historicActivityInstances) {
			highLightedActivities.add(hai.getActivityId());
		}
		if (isRuntimeProcessInstance) {
			highLightedActivities.addAll(processEngine.getRuntimeService().getActiveActivityIds(processInstanceId));
		}

		List<String> highLightedFlows = getHighLightedFlows(highLightedActivities,
				((RepositoryServiceImpl) processEngine.getRepositoryService())
						.getDeployedProcessDefinition(processDefinitionId));

		Context.setProcessEngineConfiguration(((ProcessEngineImpl) processEngine).getProcessEngineConfiguration());
		return processEngine
				.getProcessEngineConfiguration()
				.getProcessDiagramGenerator()
				.generateDiagram(processEngine.getRepositoryService().getBpmnModel(processDefinitionId), "png",
						highLightedActivities, highLightedFlows,
						processEngine.getProcessEngineConfiguration().getActivityFontName(),
						processEngine.getProcessEngineConfiguration().getLabelFontName(),
						processEngine.getProcessEngineConfiguration().getClassLoader(), 1.0);
	}

	private static List<String> getHighLightedFlows(List<String> highLightedActivities,
			ReadOnlyProcessDefinition processDefinition) {
		List<String> highLightedFlows = Lists.newArrayList();
		PvmActivity currentActivity;
		for (int i = 0; i < highLightedActivities.size() - 1; i++) {
			currentActivity = processDefinition.findActivity(highLightedActivities.get(i));
			for (PvmTransition pvmTransition : currentActivity.getOutgoingTransitions()) {
				if (pvmTransition.getDestination().getId().equals(highLightedActivities.get(i + 1))) {
					highLightedFlows.add(pvmTransition.getId());
				}
			}
		}
		return highLightedFlows;
	}

	/**
	 * 由任务对象获取流程实例businessKey
	 * 
	 * @param task
	 * @return
	 */
	public static WorkFlowBusinessModel getBusinessModel(Task task) {
		Assert.notNull(task);
		ProcessInstance processInstance = processEngine.getRuntimeService().createProcessInstanceQuery()
				.processInstanceId(task.getProcessInstanceId()).singleResult();
		return processInstance != null ? (WorkFlowBusinessModel) ObjectUuidUtils.getObject(processInstance
				.getBusinessKey()) : null;
	}

	/**
	 * 由历史任务对象获取流程实例businessKey
	 * 
	 * @param task
	 * @return
	 */
	public static WorkFlowBusinessModel getBusinessModel(HistoricTaskInstance task) {
		Assert.notNull(task);
		HistoricProcessInstance processInstance = processEngine.getHistoryService()
				.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
		return processInstance != null ? (WorkFlowBusinessModel) ObjectUuidUtils.getObject(processInstance
				.getBusinessKey()) : null;
	}

	/**
	 * 获取指定流程实例的历史执行记录
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public static List<HistoricTaskInstance> getHistoricTaskInstances(String processInstanceId) {
		Assert.hasLength(processInstanceId);
		return processEngine.getHistoryService().createHistoricTaskInstanceQuery().processInstanceId(processInstanceId)
				.includeTaskLocalVariables().orderByHistoricTaskInstanceStartTime().asc().list();
	}

	// 毫秒格式化
	private static PeriodFormatter formatter = new PeriodFormatterBuilder().appendDays().appendSuffix("d")
			.appendHours().appendSuffix("h").appendMinutes().appendSuffix("m").appendSeconds().appendSuffix("s")
			.toFormatter();

	public static String formatDuration(long durationInMillis) {
		return formatter.print(new Duration(durationInMillis).toPeriod());
	}
}
