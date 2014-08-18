package com.minyisoft.webapp.yjmz.common.model.enumField;

import com.minyisoft.webapp.core.model.enumField.DescribableEnum;
import com.minyisoft.webapp.core.model.enumField.DescribableEnumHelper;

public enum SupportedWorkFlowTypeEnum implements DescribableEnum<Integer> {
	BUG_RECOVERY(0), // 系统错误修复
	FEATURE_DEVELOPMENT(1), // 系统功能开发
	GENERAL(2);// 事务任务

	private Integer typeValue;

	private SupportedWorkFlowTypeEnum(Integer tValue) {
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
