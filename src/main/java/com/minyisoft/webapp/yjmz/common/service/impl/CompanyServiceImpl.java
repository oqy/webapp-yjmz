package com.minyisoft.webapp.yjmz.common.service.impl;

import org.springframework.stereotype.Service;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.model.criteria.CompanyCriteria;
import com.minyisoft.webapp.yjmz.common.model.CompanyInfo;
import com.minyisoft.webapp.yjmz.common.persistence.CompanyDao;
import com.minyisoft.webapp.yjmz.common.service.CompanyService;

@Service("companyService")
public class CompanyServiceImpl extends BaseServiceImpl<CompanyInfo,CompanyCriteria,CompanyDao> implements CompanyService {
}
