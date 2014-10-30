package com.minyisoft.webapp.yjmz.common.service.impl;

import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.google.common.collect.Maps;
import com.minyisoft.webapp.core.exception.ServiceException;
import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.model.criteria.PageDevice;
import com.minyisoft.webapp.core.service.utils.ServiceUtils;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.yjmz.common.model.CompanyWorkFlowBillBaseInfo;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowConfigInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.WorkFlowConfigCriteria;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowProcessStatusEnum;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowStatusEnum;
import com.minyisoft.webapp.yjmz.common.security.SecurityUtils;
import com.minyisoft.webapp.yjmz.common.service.WorkFlowConfigService;
import com.minyisoft.webapp.yjmz.common.service.WorkFlowProcessService;

@Service("workFlowProcessService")
public class WorkFlowProcessServiceImpl implements WorkFlowProcessService {
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private IdentityService identityService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private WorkFlowConfigService workFlowConfigService;
	@Autowired
	private RepositoryService repositoryService;

	@Override
	public List<HistoricProcessInstance> getHistoricProcessInstances(String processDefinitionId, PageDevice pageDevice) {
		Assert.hasText(processDefinitionId, "流程定义ID不能为空");
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(processDefinitionId).singleResult();
		Assert.notNull(processDefinition, "无法获取指定id对应的流程定义");

		// 获取包含相同processDefinitionKey的所有历史流程，避免流程重新发布后无法获取前一版本流程定义的实例
		HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery()
				.processDefinitionKey(processDefinition.getKey()).finished().orderByProcessInstanceStartTime().desc();
		if (pageDevice != null) {
			pageDevice.setTotalRecords((int) query.count());
			return query.listPage(pageDevice.getStartRowNumberOfCurrentPage() - 1, pageDevice.getRecordsPerPage());
		} else {
			return query.list();
		}
	}

	@Override
	public void startProcess(ISystemOrgObject owner, WorkFlowBusinessModel businessModel) {
		Assert.notNull(owner, "未指定工作流程定义所属组织");
		Assert.notNull(businessModel, "不存在指定的待启动工作流业务对象");
		Assert.isTrue(businessModel.isProcessUnStarted(), "指定业务对象已启动工作流，无需重复启动");

		WorkFlowConfigCriteria criteria = new WorkFlowConfigCriteria();
		criteria.setWorkFlowStatus(WorkFlowStatusEnum.NORMAL);
		criteria.setDefineOrg(owner);
		criteria.setWorkFlowType(businessModel.getClass());
		List<WorkFlowConfigInfo> configs = workFlowConfigService.getCollection(criteria);

		WorkFlowConfigInfo targetCoinfig = null;// 待启动的工作流配置信息
		StandardEvaluationContext context = new StandardEvaluationContext(businessModel);
		ExpressionParser parser = new SpelExpressionParser();
		for (WorkFlowConfigInfo config : configs) {
			if (StringUtils.isNotBlank(config.getProcessDefinitionId())// 当前流程id不为空
					&& (StringUtils.isBlank(config.getTriggerExpression()) || parser.parseExpression(
							config.getTriggerExpression()).getValue(context, Boolean.class))// 当前流程无触发条件或当前业务对象符合触发条件
					&& (targetCoinfig == null// 待启动流程为空
							|| (StringUtils.isBlank(targetCoinfig.getTriggerExpression()) && !StringUtils
									.isBlank(config.getTriggerExpression())) // 或待启动流程无触发条件，现流程存在触发条件
					|| (targetCoinfig.getCreateDate().before(config.getCreateDate()))))// 或待启动流程创建时间早于当前流程创建时间
			{
				targetCoinfig = config;
			}
		}

		if (targetCoinfig != null) {
			// 启动工作流
			try {
				Map<String, Object> processVariables = Maps.newHashMap();
				processVariables.put(businessModel.getBusinessModelProcessVariableName(), businessModel);
				identityService.setAuthenticatedUserId(SecurityUtils.getCurrentUser().getCellPhoneNumber());
				runtimeService.startProcessInstanceById(targetCoinfig.getProcessDefinitionId(), businessModel.getId(),
						processVariables);
				return;
			} finally {
				identityService.setAuthenticatedUserId(null);
			}
		}
		throw new ServiceException(owner.getName() + "并没有为指定的"
				+ ObjectUuidUtils.getClassLabel(businessModel.getClass()) + "设置满足启动条件的工作流程");
	}

	@Override
	public void deleteRunningProcess(String processInstanceId, String deleteReason) {
		SecurityUtils.checkIsCurrentUserAdministrator();
		Assert.hasText(processInstanceId, "流程定义ID不能为空");
		HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		if (processInstance != null) {
			// 清空CompanyWorkFlowBillBaseInfo的processInstanceId字段
			IModelObject model = ServiceUtils.getModel(processInstance.getBusinessKey());
			if (model instanceof CompanyWorkFlowBillBaseInfo) {
				((CompanyWorkFlowBillBaseInfo) model).setProcessInstanceId(null);
				((CompanyWorkFlowBillBaseInfo) model).setProcessStatus(WorkFlowProcessStatusEnum.UNSTARTED);
				ServiceUtils.getService(model.getClass()).save(model);
			}
			runtimeService.deleteProcessInstance(processInstanceId, deleteReason);
			historyService.deleteHistoricProcessInstance(processInstanceId);
		}
	}

	@Override
	public List<ProcessInstance> getProcessInstances(String processDefinitionId, PageDevice pageDevice) {
		Assert.hasText(processDefinitionId, "流程定义ID不能为空");
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(processDefinitionId).singleResult();
		Assert.notNull(processDefinition, "无法获取指定id对应的流程定义");

		// 获取包含相同processDefinitionKey的所有运行流程，避免流程重新发布后无法获取前一版本流程定义正在运行的实例
		ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery()
				.processDefinitionKey(processDefinition.getKey()).orderByProcessInstanceId().desc();
		if (pageDevice != null) {
			pageDevice.setTotalRecords((int) query.count());
			return query.listPage(pageDevice.getStartRowNumberOfCurrentPage() - 1, pageDevice.getRecordsPerPage());
		} else {
			return query.list();
		}
	}
}
