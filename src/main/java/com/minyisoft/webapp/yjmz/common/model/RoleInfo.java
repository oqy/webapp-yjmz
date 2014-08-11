package com.minyisoft.webapp.yjmz.common.model;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.core.model.DataBaseInfo;
import com.minyisoft.webapp.core.model.ISystemRoleObject;

/**
 * @author qingyong_ou 系统角色
 */
@Getter
@Setter
@ModelKey(0x147C41F32BEL)
public class RoleInfo extends DataBaseInfo implements ISystemRoleObject {
	// 角色标记值
	private String value;
}
