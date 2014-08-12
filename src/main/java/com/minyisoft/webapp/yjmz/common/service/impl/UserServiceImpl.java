package com.minyisoft.webapp.yjmz.common.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.model.PermissionInfo;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.model.criteria.UserCriteria;
import com.minyisoft.webapp.yjmz.common.model.RoleInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.persistence.UserDao;
import com.minyisoft.webapp.yjmz.common.service.UserService;

@Service("userService")
public class UserServiceImpl extends BaseServiceImpl<UserInfo, UserCriteria, UserDao> implements UserService {

	@Override
	public List<RoleInfo> getUserRoles(UserInfo user, ISystemOrgObject org) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PermissionInfo> getUserPermissions(UserInfo user, ISystemOrgObject org) {
		// TODO Auto-generated method stub
		return null;
	}
}
