package com.minyisoft.webapp.yjmz.common.model.enumField;

import com.minyisoft.webapp.core.model.enumField.DescribableEnumHelper;
import com.minyisoft.webapp.core.model.enumField.DescribableEnum;

public enum CompanyStatusEnum implements DescribableEnum<Integer> {
		NORMAL(0), // 正常
		REPEAL(1); // 已撤销
		
	private Integer typeValue;
	
	private CompanyStatusEnum(Integer tValue){
		this.typeValue=tValue;
	}

	public String getDescription() {
		return DescribableEnumHelper.getDescription(this);
	}

	public Integer getValue() {
		return typeValue;
	}
	
	public String toString(){
				return String.valueOf(typeValue);
			}
}
