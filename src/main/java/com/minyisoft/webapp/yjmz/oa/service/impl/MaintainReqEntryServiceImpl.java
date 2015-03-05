package com.minyisoft.webapp.yjmz.oa.service.impl;

import org.springframework.stereotype.Service;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.oa.model.criteria.MaintainReqEntryCriteria;
import com.minyisoft.webapp.yjmz.oa.model.MaintainReqEntryInfo;
import com.minyisoft.webapp.yjmz.oa.persistence.MaintainReqEntryDao;
import com.minyisoft.webapp.yjmz.oa.service.MaintainReqEntryService;

@Service("maintainReqEntryService")
public class MaintainReqEntryServiceImpl extends BaseServiceImpl<MaintainReqEntryInfo,MaintainReqEntryCriteria,MaintainReqEntryDao> implements MaintainReqEntryService {
}
