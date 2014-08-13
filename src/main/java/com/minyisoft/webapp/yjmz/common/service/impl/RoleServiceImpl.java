package com.minyisoft.webapp.yjmz.common.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.minyisoft.webapp.core.model.PermissionInfo;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.model.RoleInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.RoleCriteria;
import com.minyisoft.webapp.yjmz.common.persistence.PermissionDao;
import com.minyisoft.webapp.yjmz.common.persistence.RoleDao;
import com.minyisoft.webapp.yjmz.common.service.RoleService;

@Service("roleService")
public class RoleServiceImpl extends BaseServiceImpl<RoleInfo, RoleCriteria, RoleDao> implements RoleService {
	@Autowired
	private PermissionDao permissionDao;

	@Override
	public void delete(RoleInfo role) {
		super.delete(role);
		getBaseDao().deleteRolePermission(role);
		getBaseDao().deleteRoleUser(role);
	}

	@Override
	public void submitRoleWithPermission(RoleInfo role, List<PermissionInfo> permissions) {
		if (role == null) {
			return;
		}
		submit(role);

		// 首先删除角色关联的所有权限
		getBaseDao().deleteRolePermission(role);
		// 设置角色对应的权限信息
		if (CollectionUtils.isEmpty(permissions)) {
			return;
		}
		for (PermissionInfo permission : permissions) {
			getBaseDao().insertRolePermission(role, permission);
		}
	}

	@Override
	public List<PermissionInfo> getRolePermissions(RoleInfo role) {
		if (role == null || !role.isIdPresented()) {
			return Collections.emptyList();
		}
		return permissionDao.getRolePermission(role);
	}
}
