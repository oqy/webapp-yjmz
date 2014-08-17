package com.minyisoft.webapp.yjmz.oa.service.impl;

import org.springframework.stereotype.Service;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.oa.model.criteria.PurchaseReqBillCriteria;
import com.minyisoft.webapp.yjmz.oa.model.PurchaseReqBillInfo;
import com.minyisoft.webapp.yjmz.oa.persistence.PurchaseReqBillDao;
import com.minyisoft.webapp.yjmz.oa.service.PurchaseReqBillService;

@Service("purchaseReqBillService")
public class PurchaseReqBillServiceImpl extends BaseServiceImpl<PurchaseReqBillInfo,PurchaseReqBillCriteria,PurchaseReqBillDao> implements PurchaseReqBillService {
}
