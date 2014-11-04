package com.minyisoft.webapp.yjmz.oa.model.enumField;

import com.minyisoft.webapp.core.model.enumField.DescribableEnumHelper;
import com.minyisoft.webapp.core.model.enumField.DescribableEnum;

public enum AcceptanceStatusEnum implements DescribableEnum<Integer> {
	NO_NEED(0), // 无需验收
	UNCOMMITTED(1), // 未提交
	RUNNING(2), // 验收中
	FINISHED(3); // 验收完毕

	private Integer typeValue;

	private AcceptanceStatusEnum(Integer tValue) {
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
