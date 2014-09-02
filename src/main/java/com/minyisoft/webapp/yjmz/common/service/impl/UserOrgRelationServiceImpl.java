package com.minyisoft.webapp.yjmz.common.service.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minyisoft.webapp.core.service.CUDPostProcessor;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.model.CompanyInfo;
import com.minyisoft.webapp.yjmz.common.model.RoleInfo;
import com.minyisoft.webapp.yjmz.common.model.UserOrgRelationInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.UserOrgRelationCriteria;
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
	private UserOrgRelationCUDPostProcessor processor = new UserOrgRelationCUDPostProcessor();

	@Override
	protected CUDPostProcessor<?>[] getPostProcessors() {
		return new CUDPostProcessor<?>[] { processor };
	}

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

	@Override
	protected boolean useModelCache() {
		return true;
	}

	private class UserOrgRelationCUDPostProcessor implements CUDPostProcessor<UserOrgRelationInfo> {

		@Override
		public void processAferAddNew(UserOrgRelationInfo info) {
			if (info.getOrg() instanceof CompanyInfo) {
				getCacheManager().getModelCache(info.getOrg().getClass()).evict(info.getOrg().getId());
			}
			getCacheManager().getModelCache(info.getUser().getClass()).evict(info.getUser().getId());
		}

		@Override
		public void processAfterSave(UserOrgRelationInfo info) {
			if (info.getOrg() instanceof CompanyInfo) {
				getCacheManager().getModelCache(info.getOrg().getClass()).evict(info.getOrg().getId());
			}
			getCacheManager().getModelCache(info.getUser().getClass()).evict(info.getUser().getId());
		}

		@Override
		public void processAfterDelete(UserOrgRelationInfo info) {
			if (info.getOrg() instanceof CompanyInfo) {
				getCacheManager().getModelCache(info.getOrg().getClass()).evict(info.getOrg().getId());
			}
			getCacheManager().getModelCache(info.getUser().getClass()).evict(info.getUser().getId());
		}

		@Override
		public boolean canProcess(Class<?> targetType) {
			return true;
		}

	}
}
