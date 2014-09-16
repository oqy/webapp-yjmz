package com.minyisoft.webapp.yjmz.common.util.workflow;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.AbstractBpmnParseHandler;
import org.springframework.stereotype.Component;

@Component
public class GlobalStartEventBpmnParseHandler extends AbstractBpmnParseHandler<StartEvent> {
	private static final StartEventCompleteListener startEventCompleteListener = new StartEventCompleteListener();

	@Override
	protected Class<? extends BaseElement> getHandledType() {
		return StartEvent.class;
	}

	@Override
	protected void executeParse(BpmnParse bpmnParse, StartEvent element) {
		bpmnParse.getCurrentActivity().addExecutionListener(org.activiti.engine.impl.pvm.PvmEvent.EVENTNAME_END,
				startEventCompleteListener);
	}

}
