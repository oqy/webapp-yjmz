package com.minyisoft.webapp.yjmz.common.model.enumField;

import com.minyisoft.webapp.core.model.enumField.DescribableEnumHelper;
import com.minyisoft.webapp.core.model.enumField.DescribableEnum;

public enum WorkFlowProcessStatusEnum implements DescribableEnum<Integer> {
	UNSTARTED(0), // 未启动
	RUNNING(1), // 运行中
	FINISHED(2); // 已结束

	private Integer typeValue;

	private WorkFlowProcessStatusEnum(Integer tValue) {
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
