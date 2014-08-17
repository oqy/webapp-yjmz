package com.minyisoft.webapp.yjmz.oa.service.impl;

import org.springframework.stereotype.Service;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.oa.model.criteria.ReportCriteria;
import com.minyisoft.webapp.yjmz.oa.model.ReportInfo;
import com.minyisoft.webapp.yjmz.oa.persistence.ReportDao;
import com.minyisoft.webapp.yjmz.oa.service.ReportService;

@Service("reportService")
public class ReportServiceImpl extends BaseServiceImpl<ReportInfo,ReportCriteria,ReportDao> implements ReportService {
}
