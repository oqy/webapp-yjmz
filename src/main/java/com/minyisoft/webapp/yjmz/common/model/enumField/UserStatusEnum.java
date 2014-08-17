package com.minyisoft.webapp.yjmz.common.model.enumField;

import com.minyisoft.webapp.core.model.enumField.DescribableEnumHelper;
import com.minyisoft.webapp.core.model.enumField.DescribableEnum;

public enum UserStatusEnum implements DescribableEnum<Integer> {
		INCUMBENCY(0), // 在职
		DIMISSION(1); // 离职
		
	private Integer typeValue;
	
	private UserStatusEnum(Integer tValue){
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
