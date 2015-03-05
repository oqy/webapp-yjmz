package com.minyisoft.webapp.yjmz.common.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.minyisoft.webapp.core.service.CUDPostProcessor;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.model.CompanyInfo;
import com.minyisoft.webapp.yjmz.common.model.DepartmentInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.DepartmentCriteria;
import com.minyisoft.webapp.yjmz.common.persistence.DepartmentDao;
import com.minyisoft.webapp.yjmz.common.persistence.UserOrgRelationDao;
import com.minyisoft.webapp.yjmz.common.service.DepartmentService;

@Service("departmentService")
public class DepartmentServiceImpl extends BaseServiceImpl<DepartmentInfo, DepartmentCriteria, DepartmentDao> implements
		DepartmentService {
	@Autowired
	private UserOrgRelationDao userOrgRelationDao;

	public DepartmentServiceImpl() {
		List<CUDPostProcessor<?>> processors = Lists.newArrayList();
		processors.add(new DepartmentCUDPostProcessor());
		setPostProcessors(processors);
	}

	@Override
	public void delete(DepartmentInfo info) {
		super.delete(info);
		userOrgRelationDao.replaceDepartment(info, null);
	}

	@Override
	protected boolean useModelCache() {
		return true;
	}

	private class DepartmentCUDPostProcessor implements CUDPostProcessor<DepartmentInfo> {

		@Override
		public void processAferAddNew(DepartmentInfo info) {
			if (info.getOrg() instanceof CompanyInfo) {
				getCacheManager().getModelCache(info.getOrg().getClass()).evict(info.getOrg().getId());
			}
		}

		@Override
		public void processAfterSave(DepartmentInfo info) {
			if (info.getOrg() instanceof CompanyInfo) {
				getCacheManager().getModelCache(info.getOrg().getClass()).evict(info.getOrg().getId());
			}
		}

		@Override
		public void processAfterDelete(DepartmentInfo info) {
			if (info.getOrg() instanceof CompanyInfo) {
				getCacheManager().getModelCache(info.getOrg().getClass()).evict(info.getOrg().getId());
			}
		}

		@Override
		public boolean canProcess(Class<?> targetType) {
			return true;
		}

	}
}
