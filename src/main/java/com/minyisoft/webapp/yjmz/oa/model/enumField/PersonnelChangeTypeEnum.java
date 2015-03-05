package com.minyisoft.webapp.yjmz.oa.model.enumField;

import com.minyisoft.webapp.core.model.enumField.DescribableEnumHelper;
import com.minyisoft.webapp.core.model.enumField.DescribableEnum;

public enum PersonnelChangeTypeEnum implements DescribableEnum<Integer> {
	PROBATION_FINISH(0), // 完成试用
	PROMOTION(1), // 升职
	INTERNAL_TRANSFER(2), // 内部调动
	SALARY_ADJUSTMENT(3), // 工资调整
	OTHER(4); // 其他

	private Integer typeValue;

	private PersonnelChangeTypeEnum(Integer tValue) {
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
