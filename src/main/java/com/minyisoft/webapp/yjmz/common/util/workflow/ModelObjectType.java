package com.minyisoft.webapp.yjmz.common.util.workflow;

import org.activiti.engine.impl.variable.ValueFields;
import org.activiti.engine.impl.variable.VariableType;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;

public class ModelObjectType implements VariableType {

	@Override
	public String getTypeName() {
		return "ModelObject";
	}

	@Override
	public boolean isCachable() {
		return false;
	}

	@Override
	public boolean isAbleToStore(Object value) {
		return value instanceof IModelObject;
	}

	@Override
	public void setValue(Object value, ValueFields valueFields) {
		valueFields.setTextValue(value == null ? null : ((IModelObject) value).getId());
	}

	@Override
	public Object getValue(ValueFields valueFields) {
		return ObjectUuidUtils.getObject(valueFields.getTextValue());
	}

}
