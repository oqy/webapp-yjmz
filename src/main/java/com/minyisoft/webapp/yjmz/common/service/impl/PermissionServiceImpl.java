package com.minyisoft.webapp.yjmz.common.service.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.minyisoft.webapp.core.model.PermissionInfo;
import com.minyisoft.webapp.core.security.utils.PermissionUtils;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.core.utils.spring.cache.ModelObjectCacheManager;
import com.minyisoft.webapp.core.utils.spring.cache.ModelObjectCacheType;
import com.minyisoft.webapp.yjmz.common.model.criteria.PermissionCriteria;
import com.minyisoft.webapp.yjmz.common.persistence.PermissionDao;
import com.minyisoft.webapp.yjmz.common.service.PermissionService;

@Service("permissionService")
public class PermissionServiceImpl extends BaseServiceImpl<PermissionInfo, PermissionCriteria, PermissionDao> implements
		PermissionService {

	@Override
	@ModelObjectCacheType(modelType = PermissionInfo.class)
	@CacheEvict(value = ModelObjectCacheManager.DUMMY_CACHE, allEntries = true)
	public void initPermission() {
		// 首先删除现有的所有权限信息
		getBaseDao().flushPermission();
		// 获取权限信息
		for (PermissionInfo permission : PermissionUtils.getSystemPermissionList()) {
			addNew(permission);
		}
		// 删除多余的角色-权限配对信息
		getBaseDao().deleteUselessRolePermission();
	}

	@Override
	protected boolean useModelCache() {
		return true;
	}
}
