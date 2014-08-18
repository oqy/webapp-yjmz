package com.minyisoft.webapp.yjmz.common.model.enumField;

import com.minyisoft.webapp.core.model.enumField.DescribableEnum;
import com.minyisoft.webapp.core.model.enumField.DescribableEnumHelper;

public enum WorkFlowStatusEnum implements DescribableEnum<Integer> {
	NORMAL(0), // 正常
	SUSPEND(1); // 挂起

	private Integer typeValue;

	private WorkFlowStatusEnum(Integer tValue) {
		this.typeValue = tValue;
	}

	public String getDescription() {
		return DescribableEnumHelper.getDescription(this);
	}

	public Integer getValue() {
		return typeValue;
	}

	public String toString() {
		return String.valueOf(typeValue);
	}
}
