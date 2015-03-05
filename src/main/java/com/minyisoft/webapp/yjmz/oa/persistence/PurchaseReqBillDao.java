package com.minyisoft.webapp.yjmz.oa.persistence;

import org.apache.ibatis.annotations.Param;

import com.minyisoft.webapp.core.persistence.BaseDao;
import com.minyisoft.webapp.yjmz.oa.model.PurchaseReqBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.criteria.PurchaseReqBillCriteria;
import com.minyisoft.webapp.yjmz.oa.model.entity.PurchaseProcessReportEntity;

public interface PurchaseReqBillDao extends BaseDao<PurchaseReqBillInfo, PurchaseReqBillCriteria> {
	int addProcessReport(@Param("reqBill") PurchaseReqBillInfo reqBill,
			@Param("processReport") PurchaseProcessReportEntity processReport);
}
