package com.minyisoft.webapp.yjmz.common.service.impl;

import org.springframework.stereotype.Service;

import com.minyisoft.webapp.core.exception.ServiceException;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.model.criteria.CompanyCriteria;
import com.minyisoft.webapp.yjmz.common.model.CompanyInfo;
import com.minyisoft.webapp.yjmz.common.persistence.CompanyDao;
import com.minyisoft.webapp.yjmz.common.service.CompanyService;

@Service("companyService")
public class CompanyServiceImpl extends BaseServiceImpl<CompanyInfo,CompanyCriteria,CompanyDao> implements CompanyService {
	@Override
	public void delete(CompanyInfo info) {
		throw new ServiceException("不允许删除公司信息");
	}
	
	@Override
	protected boolean useModelCache() {
		return true;
	}
}
