package com.minyisoft.webapp.yjmz.common.model;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.model.ISystemOrgObject;

/**
 * @author qingyong_ou 用户对应组织关系
 */
@Getter
@Setter
public class UserOrgEntity {
	// 所在组织架构
	private ISystemOrgObject org;
	// 上级用户
	private UserInfo upperUser;
	// 用户路径
	private String userPath;
}
