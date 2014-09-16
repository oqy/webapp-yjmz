package com.minyisoft.webapp.yjmz.common.util.workflow;

import lombok.Setter;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.AbstractBpmnParseHandler;
import org.springframework.stereotype.Component;

@Setter
@Component
public class GlobalEndEventBpmnParseHandler extends AbstractBpmnParseHandler<EndEvent> {
	private static final EndEventCompleteListener endEventCompleteListener = new EndEventCompleteListener();

	@Override
	protected Class<? extends BaseElement> getHandledType() {
		return EndEvent.class;
	}

	@Override
	protected void executeParse(BpmnParse bpmnParse, EndEvent element) {
		bpmnParse.getCurrentActivity().addExecutionListener(org.activiti.engine.impl.pvm.PvmEvent.EVENTNAME_END,
				endEventCompleteListener);
	}

}
