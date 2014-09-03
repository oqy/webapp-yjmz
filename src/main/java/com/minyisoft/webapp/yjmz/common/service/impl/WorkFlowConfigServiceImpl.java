package com.minyisoft.webapp.yjmz.common.service.impl;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.google.common.collect.Maps;
import com.minyisoft.webapp.core.exception.ServiceException;
import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.model.criteria.PageDevice;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.core.service.utils.ServiceUtils;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowConfigInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.WorkFlowConfigCriteria;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowStatusEnum;
import com.minyisoft.webapp.yjmz.common.persistence.WorkFlowConfigDao;
import com.minyisoft.webapp.yjmz.common.security.SecurityUtils;
import com.minyisoft.webapp.yjmz.common.service.WorkFlowConfigService;

@Service("workFlowConfigService")
public class WorkFlowConfigServiceImpl extends
		BaseServiceImpl<WorkFlowConfigInfo, WorkFlowConfigCriteria, WorkFlowConfigDao> implements WorkFlowConfigService {
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private IdentityService identityService;
	@Autowired
	private HistoryService historyService;

	@Override
	protected void _validateDataBeforeDelete(WorkFlowConfigInfo info) {
		SecurityUtils.checkIsCurrentUserAdministrator();
		Assert.isTrue(info.getWorkFlowStatus() != WorkFlowStatusEnum.NORMAL,
				"不允许删除状态为[" + WorkFlowStatusEnum.NORMAL.getDescription() + "]的工作流");
	}

	@Override
	protected void _validateDataBeforeAdd(WorkFlowConfigInfo info) {
		SecurityUtils.checkIsCurrentUserAdministrator();
	}

	@Override
	protected void _validateDataBeforeSave(WorkFlowConfigInfo info) {
		SecurityUtils.checkIsCurrentUserAdministrator();
	}

	@Override
	public void delete(WorkFlowConfigInfo info) {
		super.delete(info);
		// 删除流程定义
		repositoryService.deleteDeployment(repositoryService.getProcessDefinition(info.getProcessDefinitionId())
				.getDeploymentId(), true);
	}

	@Override
	public void deployWorkFlow(WorkFlowConfigInfo config, String processDefinitionFileName,
			InputStream processDefinitionFile) {
		DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
				.category(config.getDefineOrg().getId()).enableDuplicateFiltering().name("minyisoftWorkFlowDeployment");
		deploymentBuilder.addInputStream(processDefinitionFileName, processDefinitionFile);
		Deployment deploy = deploymentBuilder.deploy();
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId())
				.singleResult();

		config.setProcessDefinitionId(pd.getId());
		config.setName(pd.getName());
		submit(config);
	}

	@Override
	public void activateProcessDefinition(WorkFlowConfigInfo config) {
		Assert.notNull(config, "待激活工作流不能为空");
		Assert.isTrue(config.getWorkFlowStatus() == WorkFlowStatusEnum.SUSPEND, "待激活工作流状态异常");

		config.setWorkFlowStatus(WorkFlowStatusEnum.NORMAL);
		save(config);
		repositoryService.activateProcessDefinitionById(config.getProcessDefinitionId(), true, null);
	}

	@Override
	public void suspendProcessDefinition(WorkFlowConfigInfo config) {
		Assert.notNull(config, "待挂起工作流不能为空");
		Assert.isTrue(config.getWorkFlowStatus() == WorkFlowStatusEnum.NORMAL, "待挂起工作流状态异常");

		config.setWorkFlowStatus(WorkFlowStatusEnum.SUSPEND);
		save(config);
		repositoryService.suspendProcessDefinitionById(config.getProcessDefinitionId(), true, null);
	}

	@Override
	public List<HistoricProcessInstance> getProcessInstances(String processDefinitionId, PageDevice pageDevice) {
		Assert.hasText(processDefinitionId, "流程定义ID不能为空");
		HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery()
				.processDefinitionId(processDefinitionId).orderByProcessInstanceId().desc();
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
		List<WorkFlowConfigInfo> configs = getCollection(criteria);

		StandardEvaluationContext context = new StandardEvaluationContext(businessModel);
		ExpressionParser parser = new SpelExpressionParser();
		for (WorkFlowConfigInfo config : configs) {
			if (StringUtils.isNotBlank(config.getProcessDefinitionId())
					&& (StringUtils.isBlank(config.getTriggerExpression()) || parser.parseExpression(
							config.getTriggerExpression()).getValue(context, Boolean.class))) {
				// 启动工作流
				try {
					Map<String, Object> processVariables = Maps.newHashMap();
					processVariables.put(businessModel.getBusinessModelProcessVariableName(), businessModel);
					identityService.setAuthenticatedUserId(SecurityUtils.getCurrentUser().getCellPhoneNumber());
					ProcessInstance instance = runtimeService.startProcessInstanceById(config.getProcessDefinitionId(),
							businessModel.getId(), processVariables);

					// 保存工作流程实例id
					businessModel.setProcessInstanceId(instance.getId());
					ServiceUtils.getService(businessModel.getClass()).save(businessModel);
					return;
				} catch (Exception e) {
					throw new ServiceException(e.getMessage(), e);
				} finally {
					identityService.setAuthenticatedUserId(null);
				}
			}
		}
		throw new ServiceException(owner.getName() + "并没有为指定的"
				+ ObjectUuidUtils.getClassLabel(businessModel.getClass()) + "设置满足启动条件的工作流程");
	}

	@Override
	public void deleteRunningProcess(String processInstanceId) {
		runtimeService.deleteProcessInstance(processInstanceId, null);
		historyService.deleteHistoricProcessInstance(processInstanceId);
	}
}
