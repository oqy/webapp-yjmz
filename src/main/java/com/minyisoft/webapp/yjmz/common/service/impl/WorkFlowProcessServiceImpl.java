package com.minyisoft.webapp.yjmz.common.service.impl;

import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
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
import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.model.criteria.PageDevice;
import com.minyisoft.webapp.core.service.utils.ServiceUtils;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowConfigInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.WorkFlowConfigCriteria;
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

	@Override
	public List<HistoricProcessInstance> getHistoricProcessInstances(String processDefinitionId, PageDevice pageDevice) {
		Assert.hasText(processDefinitionId, "流程定义ID不能为空");
		HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery()
				.processDefinitionId(processDefinitionId).finished().orderByProcessDefinitionId().desc();
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
	public void deleteRunningProcess(String processInstanceId, String deleteReason) {
		SecurityUtils.checkIsCurrentUserAdministrator();
		Assert.hasText(processInstanceId, "流程定义ID不能为空");
		HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		if (processInstance != null) {
			// 清空WorkFlowBusinessModel的processInstanceId字段
			WorkFlowBusinessModel model = (WorkFlowBusinessModel) ObjectUuidUtils.getObject(processInstance
					.getBusinessKey());
			if (model != null) {
				model.setProcessInstanceId(null);
				ServiceUtils.getService(model.getClass()).save(model);
			}
			runtimeService.deleteProcessInstance(processInstanceId, deleteReason);
			historyService.deleteHistoricProcessInstance(processInstanceId);
		}
	}

	@Override
	public List<ProcessInstance> getProcessInstances(String processDefinitionId, PageDevice pageDevice) {
		Assert.hasText(processDefinitionId, "流程定义ID不能为空");
		ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery()
				.processDefinitionId(processDefinitionId).orderByProcessInstanceId().desc();
		if (pageDevice != null) {
			pageDevice.setTotalRecords((int) query.count());
			return query.listPage(pageDevice.getStartRowNumberOfCurrentPage() - 1, pageDevice.getRecordsPerPage());
		} else {
			return query.list();
		}
	}
}
