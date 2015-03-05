package com.minyisoft.webapp.yjmz.common.service.impl;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.service.CUDPostProcessor;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.model.CompanyInfo;
import com.minyisoft.webapp.yjmz.common.model.DepartmentInfo;
import com.minyisoft.webapp.yjmz.common.model.RoleInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
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

	public UserOrgRelationServiceImpl() {
		List<CUDPostProcessor<?>> processors = Lists.newArrayList();
		processors.add(new UserOrgRelationCUDPostProcessor());
		setPostProcessors(processors);
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
	public List<UserInfo> getSubordinateness(ISystemOrgObject org, UserInfo upperUser) {
		Assert.notNull(org, "所属组织不能为空");
		Assert.notNull(upperUser, "上级用户不能为空");
		UserOrgRelationCriteria criteria = new UserOrgRelationCriteria();
		criteria.setOrg(org);
		criteria.setUpperUser(upperUser);
		List<UserOrgRelationInfo> orgRelations = getCollection(criteria);
		List<UserInfo> subordinateness = Lists.newArrayList();
		for (UserOrgRelationInfo orgRelation : orgRelations) {
			subordinateness.add(orgRelation.getUser());
		}
		return subordinateness;
	}

	@Override
	protected boolean useModelCache() {
		return true;
	}

	@Override
	public Optional<UserInfo> getDepartmentLeader(DepartmentInfo department) {
		Assert.notNull(department, "部门不能为空");
		UserOrgRelationCriteria criteria = new UserOrgRelationCriteria();
		criteria.setDepartments(department);
		UserOrgRelationInfo userOrgRelation = find(criteria);
		if (userOrgRelation != null) {
			return Optional.of(userOrgRelation.getUser());
		}
		return Optional.absent();
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
			// 若删除的组织关系里组织为用户的默认登录组织，清空用户默认登录组织
			if (info.getOrg().equals(info.getUser().getDefaultLoginOrg())) {
				info.getUser().setDefaultLoginOrg(null);
				userService.save(info.getUser());
			}
		}

		@Override
		public boolean canProcess(Class<?> targetType) {
			return true;
		}

	}
}
