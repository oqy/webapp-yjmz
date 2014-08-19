package com.minyisoft.webapp.yjmz.common.model;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.core.model.EntryBaseInfo;
import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.yjmz.common.util.SystemConstant;

/**
 * @author qingyong_ou 用户对应组织关系
 */
@Getter
@Setter
@ModelKey(0x147E2F134F7L)
public class UserOrgRelationInfo extends EntryBaseInfo {
	@Label("系统用户")
	@NotNull
	private UserInfo user;
	@Label("所在组织")
	@NotNull
	private ISystemOrgObject org;
	// 所属部门
	private DepartmentInfo department;
	// 工号
	private String jobNumber;
	// 职衔
	private String title;
	// 上级用户
	private UserInfo upperUser;

	public String getUserPath() {
		if (upperUser == null) {
			return SystemConstant.ID_SEPARATOR + user.getId();
		} else {
			return upperUser.getUserPath(org) + SystemConstant.ID_SEPARATOR + user.getId();
		}
	}
}
