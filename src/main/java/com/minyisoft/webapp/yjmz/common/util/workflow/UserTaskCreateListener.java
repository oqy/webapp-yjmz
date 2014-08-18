package com.minyisoft.webapp.yjmz.common.util.workflow;

import lombok.Setter;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.IdentityLink;
import org.apache.commons.lang3.StringUtils;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.service.utils.ServiceUtils;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;
import com.minyisoft.webapp.yjmz.common.service.UserService;

@SuppressWarnings("serial")
@Setter
public class UserTaskCreateListener implements TaskListener {
	private UserService userService;

	@Override
	public void notify(DelegateTask delegateTask) {
		if (userService != null) {
			TaskEntity task = (TaskEntity) delegateTask;
			IModelObject model = ServiceUtils.getModel(task.getProcessInstance().getProcessBusinessKey());
			StringBuffer sb = new StringBuffer("您有一条新的{0}工作流任务，");
			if (model instanceof WorkFlowBusinessModel) {
				sb.append("任务名称[").append(((WorkFlowBusinessModel) model).getName()).append("]，");
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
