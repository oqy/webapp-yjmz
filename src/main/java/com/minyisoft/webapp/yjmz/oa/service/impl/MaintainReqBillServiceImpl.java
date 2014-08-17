package com.minyisoft.webapp.yjmz.oa.service.impl;

import org.springframework.stereotype.Service;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.oa.model.criteria.MaintainReqBillCriteria;
import com.minyisoft.webapp.yjmz.oa.model.MaintainReqBillInfo;
import com.minyisoft.webapp.yjmz.oa.persistence.MaintainReqBillDao;
import com.minyisoft.webapp.yjmz.oa.service.MaintainReqBillService;

@Service("maintainReqBillService")
public class MaintainReqBillServiceImpl extends BaseServiceImpl<MaintainReqBillInfo,MaintainReqBillCriteria,MaintainReqBillDao> implements MaintainReqBillService {
}
