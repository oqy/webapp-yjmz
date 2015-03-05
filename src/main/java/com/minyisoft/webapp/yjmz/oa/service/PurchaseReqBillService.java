package com.minyisoft.webapp.yjmz.oa.service;

import com.minyisoft.webapp.core.service.BaseService;
import com.minyisoft.webapp.yjmz.oa.model.PurchaseReqBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.criteria.PurchaseReqBillCriteria;

public interface PurchaseReqBillService extends BaseService<PurchaseReqBillInfo, PurchaseReqBillCriteria> {
	/**
	 * 添加采购进度报告
	 * 
	 * @param reqBill
	 * @param purchaseFinished
	 * @param reportDetail
	 */
	void addProcessReport(PurchaseReqBillInfo reqBill, boolean purchaseFinished, String reportDetail);
}
