package com.minyisoft.webapp.yjmz.oa.persistence;

import com.minyisoft.webapp.core.persistence.BaseDao;
import com.minyisoft.webapp.yjmz.oa.model.MaintainReqBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.MaintainReqEntryInfo;
import com.minyisoft.webapp.yjmz.oa.model.criteria.MaintainReqEntryCriteria;

public interface MaintainReqEntryDao extends BaseDao<MaintainReqEntryInfo, MaintainReqEntryCriteria> {
	/**
	 * 删除指定维修单所有分录
	 * @param maintainReqBill
	 * @return
	 */
	int deleteByMaintainReqBill(MaintainReqBillInfo maintainReqBill);
	
	/**
	 * 删除指定维修单无效分录
	 * @param maintainReqBill
	 * @return
	 */
	int deleteInvalidEntryByMaintainReqBill(MaintainReqBillInfo maintainReqBill);
}
