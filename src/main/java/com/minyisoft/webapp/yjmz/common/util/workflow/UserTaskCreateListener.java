package com.minyisoft.webapp.yjmz.common.util.workflow;

import lombok.Setter;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.IdentityLink;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.service.utils.ServiceUtils;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;
import com.minyisoft.webapp.yjmz.common.service.UserService;

@SuppressWarnings("serial")
@Setter
@Component
public class UserTaskCreateListener implements TaskListener {
	@Autowired
	private UserService userService;

	@Override
	public void notify(DelegateTask delegateTask) {
		if (userService != null) {
			TaskEntity task = (TaskEntity) delegateTask;
			IModelObject model = ServiceUtils.getModel(task.getExecution().getProcessBusinessKey());
			StringBuffer sb = new StringBuffer("您有一条新的{0}工作流任务，");
			if (model instanceof WorkFlowBusinessModel) {
				sb.append("任务名称[").append(((WorkFlowBusinessModel) model).getProcessInstanceName()).append("]，");
			}
			sb.append("当前任务节点[").append(delegateTask.getName()).append("]");

			if (delegateTask.getAssignee() != null) {

			} else if (!delegateTask.getCandidates().isEmpty()) {
				UserInfo user;
				for (IdentityLink link : delegateTask.getCandidates()) {
					if (StringUtils.isNotBlank(link.getUserId())) {
						user = userService.getValue(link.getUserId());
						if (user != null) {

						}
					}
				}
			}
		}
	}
}
