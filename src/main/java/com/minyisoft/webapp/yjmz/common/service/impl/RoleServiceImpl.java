package com.minyisoft.webapp.yjmz.common.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.minyisoft.webapp.core.model.PermissionInfo;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.model.RoleInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.RoleCriteria;
import com.minyisoft.webapp.yjmz.common.persistence.RoleDao;
import com.minyisoft.webapp.yjmz.common.service.RoleService;

@Service("roleService")
public class RoleServiceImpl extends BaseServiceImpl<RoleInfo, RoleCriteria, RoleDao> implements RoleService {
	@Override
	public void delete(RoleInfo role) {
		super.delete(role);
		getBaseDao().deleteRolePermission(role);
		getBaseDao().deleteRoleUser(role);
	}

	@Override
	public void addNew(RoleInfo info) {
		super.addNew(info);
		_addRolePermission(info);
	}

	@Override
	public void save(RoleInfo info) {
		super.save(info);
		_addRolePermission(info);
	}

	private void _addRolePermission(RoleInfo role) {
		// 首先删除角色关联的所有权限
		getBaseDao().deleteRolePermission(role);
		if (!CollectionUtils.isEmpty(role.getPermissions())) {
			// 设置角色对应的权限信息
			for (PermissionInfo permission : role.getPermissions()) {
				getBaseDao().insertRolePermission(role, permission);
			}
		}
	}
}
