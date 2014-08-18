package com.minyisoft.webapp.yjmz.common.util.workflow;

import lombok.Setter;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.AbstractBpmnParseHandler;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.task.TaskDefinition;

@Setter
public class GlobalUserTaskBpmnParseHandler extends AbstractBpmnParseHandler<UserTask> {
	private UserTaskCreateListener userTaskCreateListener;

	@Override
	protected Class<? extends BaseElement> getHandledType() {
		return UserTask.class;
	}

	@Override
	protected void executeParse(BpmnParse bpmnParse, UserTask element) {
		if (userTaskCreateListener != null) {
			String taskDefinitionKey = element.getId();
			TaskDefinition taskDefinition = ((ProcessDefinitionEntity) bpmnParse.getCurrentScope()
					.getProcessDefinition()).getTaskDefinitions().get(taskDefinitionKey);
			taskDefinition.addTaskListener(TaskListener.EVENTNAME_CREATE, userTaskCreateListener);
		}
	}

}
