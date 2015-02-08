package com.minyisoft.webapp.yjmz.oa.model.enumField;

import com.minyisoft.webapp.core.model.enumField.DescribableEnumHelper;
import com.minyisoft.webapp.core.model.enumField.DescribableEnum;

public enum MaintainReplyEnum implements DescribableEnum<Integer> {
	FINISH(0), // 完成
	LACK_OF_MATERIALS(1), // 缺材料
	ON_SITE_REPAIR(2), // 外修
	PURCHASE_APPLY(3); // 报损请部门申购

	private Integer typeValue;

	private MaintainReplyEnum(Integer tValue) {
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
