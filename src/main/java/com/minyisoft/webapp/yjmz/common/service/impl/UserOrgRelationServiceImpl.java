package com.minyisoft.webapp.yjmz.common.service.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.model.criteria.UserOrgRelationCriteria;
import com.minyisoft.webapp.yjmz.common.model.RoleInfo;
import com.minyisoft.webapp.yjmz.common.model.UserOrgRelationInfo;
import com.minyisoft.webapp.yjmz.common.persistence.RoleDao;
import com.minyisoft.webapp.yjmz.common.persistence.UserOrgRelationDao;
import com.minyisoft.webapp.yjmz.common.service.UserOrgRelationService;
import com.minyisoft.webapp.yjmz.common.service.UserService;

@Service("userOrgRelationService")
public class UserOrgRelationServiceImpl extends
		BaseServiceImpl<UserOrgRelationInfo, UserOrgRelationCriteria, UserOrgRelationDao> implements
		UserOrgRelationService {
	@Autowired
	private UserService userService;
	@Autowired
	private RoleDao roleDao;

	@Override
	protected void _validateDataBeforeAdd(UserOrgRelationInfo info) {
		if (info.getUser() != null && !info.getUser().isIdPresented()) {
			userService.addNew(info.getUser());
		}
		// 先删除已有组织关系信息，避免重复插入
		if (info.getUser() != null && info.getOrg() != null) {
			getBaseDao().deleteRelation(info.getUser(), info.getOrg());
		}
	}

	@Override
	public void submit(UserOrgRelationInfo userOrgRelation, RoleInfo... roles) {
		submit(userOrgRelation);
		roleDao.deleteUserRoles(userOrgRelation.getUser(), userOrgRelation.getOrg());
		if (ArrayUtils.isNotEmpty(roles)) {
			for (RoleInfo role : roles) {
				if (userOrgRelation.getOrg().equals(role.getOrg())) {
					roleDao.insertUserRole(userOrgRelation.getUser(), role);
				}
			}
		}
	}
}
