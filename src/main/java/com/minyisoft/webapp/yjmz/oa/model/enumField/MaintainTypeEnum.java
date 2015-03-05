package com.minyisoft.webapp.yjmz.oa.model.enumField;

import com.minyisoft.webapp.core.model.enumField.DescribableEnumHelper;
import com.minyisoft.webapp.core.model.enumField.DescribableEnum;

public enum MaintainTypeEnum implements DescribableEnum<Integer> {
		ROUTINE_MAINTAIN(0), // 例行维修
		ROUTINE_CARE(1), // 例行保养
		URGENT_MAINTAIN(2), // 紧急维修
		CLIENT_IN_ROOT(3); // 房内有客
		
	private Integer typeValue;
	
	private MaintainTypeEnum(Integer tValue){
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
