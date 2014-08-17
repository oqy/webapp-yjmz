package com.minyisoft.webapp.yjmz.common.model;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.springframework.util.Assert;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.core.model.EntryBaseInfo;
import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.yjmz.common.util.SystemConstant;

/**
 * @author qingyong_ou 用户对应组织关系
 */
@Getter
@ModelKey(0x147E2F134F7L)
public class UserOrgRelationInfo extends EntryBaseInfo {
	@Setter
	@Label("系统用户")
	@NotNull
	private UserInfo user;
	@Label("所在组织")
	@NotNull
	private ISystemOrgObject org;
	// 所属部门
	private DepartmentInfo department;
	// 工号
	@Setter
	private String jobNumber;
	// 职衔
	@Setter
	private String title;
	// 上级用户
	private UserInfo upperUser;

	public UserOrgRelationInfo() {

	}

	public UserOrgRelationInfo(UserInfo user, ISystemOrgObject org) {
		Assert.isTrue(user != null && org != null);
		this.user = user;
		this.org = org;
	}

	public UserOrgRelationInfo(UserInfo user, DepartmentInfo department) {
		Assert.isTrue(user != null && department != null && department.getOrg() != null);
		this.user = user;
		this.department = department;
		this.org = department.getOrg();
	}

	public UserOrgRelationInfo(UserInfo user, ISystemOrgObject org, UserInfo upperUser) {
		this(user, org);
		if (upperUser != null && upperUser.isBelongTo(org)) {
			this.upperUser = upperUser;
		}
	}

	public UserOrgRelationInfo(UserInfo user, DepartmentInfo department, UserInfo upperUser) {
		this(user, department);
		if (upperUser != null && upperUser.isBelongTo(org)) {
			this.upperUser = upperUser;
		}
	}

	public String getUserPath() {
		if (upperUser == null) {
			return SystemConstant.ID_SEPARATOR + user.getId();
		} else {
			return upperUser.getUserPath(org) + SystemConstant.ID_SEPARATOR + user.getId();
		}
	}
}
