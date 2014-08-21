package com.minyisoft.webapp.yjmz.common.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.model.criteria.DepartmentCriteria;
import com.minyisoft.webapp.yjmz.common.model.DepartmentInfo;
import com.minyisoft.webapp.yjmz.common.persistence.DepartmentDao;
import com.minyisoft.webapp.yjmz.common.persistence.UserOrgRelationDao;
import com.minyisoft.webapp.yjmz.common.service.DepartmentService;

@Service("departmentService")
public class DepartmentServiceImpl extends BaseServiceImpl<DepartmentInfo,DepartmentCriteria,DepartmentDao> implements DepartmentService {
	@Autowired
	private UserOrgRelationDao userOrgRelationDao;
	
	@Override
	public void delete(DepartmentInfo info) {
		super.delete(info);
		userOrgRelationDao.replaceDepartment(info, null);
	}
}
