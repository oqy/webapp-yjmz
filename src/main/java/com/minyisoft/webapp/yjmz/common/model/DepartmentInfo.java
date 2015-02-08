package com.minyisoft.webapp.yjmz.common.model;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.core.model.EntryBaseInfo;
import com.minyisoft.webapp.core.model.ISystemOrgObject;

/**
 * @author qingyong_ou 部门信息
 */
@Getter
@Setter
@ModelKey(0x147D9E8F2DBL)
public class DepartmentInfo extends EntryBaseInfo {
	@Label("所属组织")
	@NotNull
	private ISystemOrgObject org;
	@Label("部门名称")
	@NotNull
	private String name;
}
