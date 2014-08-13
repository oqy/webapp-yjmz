package com.minyisoft.webapp.yjmz.common.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.minyisoft.webapp.core.model.PermissionInfo;
import com.minyisoft.webapp.core.security.utils.PermissionUtils;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.model.criteria.PermissionCriteria;
import com.minyisoft.webapp.yjmz.common.persistence.PermissionDao;
import com.minyisoft.webapp.yjmz.common.service.PermissionService;

@Service("permissionService")
public class PermissionServiceImpl extends BaseServiceImpl<PermissionInfo, PermissionCriteria, PermissionDao> implements
		PermissionService {
	public int updatePermission() {
		// 首先删除现有的所有权限信息
		getBaseDao().flushPermission();
		// 获取权限信息
		List<PermissionInfo> permissionList = PermissionUtils.getSystemPermissionList();
		for (int i = 0; i < permissionList.size(); i++) {
			addNew(permissionList.get(i));
		}
		// 删除多余的角色-权限配对信息
		getBaseDao().deleteUselessRolePermission();
		return permissionList.size();
	}
}
