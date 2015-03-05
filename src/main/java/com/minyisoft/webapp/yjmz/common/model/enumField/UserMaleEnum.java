package com.minyisoft.webapp.yjmz.common.model.enumField;

import com.minyisoft.webapp.core.model.enumField.DescribableEnumHelper;
import com.minyisoft.webapp.core.model.enumField.DescribableEnum;

public enum UserMaleEnum implements DescribableEnum<Integer> {
	MALE(0), // 男
	FEMALE(1); // 女

	private Integer typeValue;

	private UserMaleEnum(Integer tValue) {
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
