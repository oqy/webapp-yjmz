package com.minyisoft.webapp.yjmz.common.service;

import com.minyisoft.webapp.core.model.PermissionInfo;
import com.minyisoft.webapp.core.service.BaseService;
import com.minyisoft.webapp.yjmz.common.model.criteria.PermissionCriteria;

public interface PermissionService extends BaseService<PermissionInfo,PermissionCriteria> {
	//同步权限项
	int updatePermission();
}
