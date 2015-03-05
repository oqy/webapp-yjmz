package com.minyisoft.webapp.yjmz.oa.persistence;

import com.minyisoft.webapp.core.persistence.BaseDao;
import com.minyisoft.webapp.yjmz.oa.model.PurchaseReqBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.PurchaseReqEntryInfo;
import com.minyisoft.webapp.yjmz.oa.model.criteria.PurchaseReqEntryCriteria;

public interface PurchaseReqEntryDao extends BaseDao<PurchaseReqEntryInfo, PurchaseReqEntryCriteria> {
	/**
	 * 删除指定采购单所有分录
	 * 
	 * @param purchaseReqBill
	 * @return
	 */
	int deleteByPurchaseReqBill(PurchaseReqBillInfo purchaseReqBill);
	
	/**
	 * 删除指定采购单无效分录
	 * 
	 * @param purchaseReqBill
	 * @return
	 */
	int deleteInvalidEntryByPurchaseReqBill(PurchaseReqBillInfo purchaseReqBill);
}
