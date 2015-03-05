package com.minyisoft.webapp.yjmz.common.util.workflow;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.form.AbstractFormType;

import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;

public class UserFormType extends AbstractFormType {

	@Override
	public String getName() {
		return "user";
	}

	@Override
	public Object convertFormValueToModelValue(String propertyValue) {
		if (ObjectUuidUtils.isLegalId(UserInfo.class, propertyValue)) {
			return ObjectUuidUtils.getObject(propertyValue);
		}
		return null;
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) {
		if (modelValue == null) {
			return null;
		} else if (modelValue instanceof UserInfo) {
			return ((UserInfo) modelValue).getId();
		}
		throw new ActivitiIllegalArgumentException("Model value is not of type " + UserInfo.class.getName()
				+ ", but of type " + modelValue.getClass().getName());
	}

}
