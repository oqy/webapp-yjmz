package com.minyisoft.webapp.yjmz.common.model;

import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.core.model.DataBaseInfo;
import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.model.ISystemRoleObject;
import com.minyisoft.webapp.core.model.PermissionInfo;

/**
 * @author qingyong_ou 系统角色
 */
@Getter
@Setter
@ModelKey(0x147C41F32BEL)
public class RoleInfo extends DataBaseInfo implements ISystemRoleObject {
	// 所属组织
	private ISystemOrgObject org;
	@Label("角色标记值")
	@NotBlank
	private String value;
	// 包含权限
	private List<PermissionInfo> permissions;
}
