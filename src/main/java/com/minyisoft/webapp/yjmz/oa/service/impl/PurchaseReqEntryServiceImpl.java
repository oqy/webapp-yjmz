package com.minyisoft.webapp.yjmz.oa.service.impl;

import org.springframework.stereotype.Service;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.oa.model.criteria.PurchaseReqEntryCriteria;
import com.minyisoft.webapp.yjmz.oa.model.PurchaseReqEntryInfo;
import com.minyisoft.webapp.yjmz.oa.persistence.PurchaseReqEntryDao;
import com.minyisoft.webapp.yjmz.oa.service.PurchaseReqEntryService;

@Service("purchaseReqEntryService")
public class PurchaseReqEntryServiceImpl extends BaseServiceImpl<PurchaseReqEntryInfo,PurchaseReqEntryCriteria,PurchaseReqEntryDao> implements PurchaseReqEntryService {
}
