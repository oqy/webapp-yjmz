package com.minyisoft.webapp.yjmz.oa.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minyisoft.webapp.core.service.impl.BillBaseServiceImpl;
import com.minyisoft.webapp.yjmz.oa.model.PurchaseReqBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.PurchaseReqEntryInfo;
import com.minyisoft.webapp.yjmz.oa.model.criteria.PurchaseReqBillCriteria;
import com.minyisoft.webapp.yjmz.oa.persistence.PurchaseReqBillDao;
import com.minyisoft.webapp.yjmz.oa.persistence.PurchaseReqEntryDao;
import com.minyisoft.webapp.yjmz.oa.service.PurchaseReqBillService;
import com.minyisoft.webapp.yjmz.oa.service.PurchaseReqEntryService;

@Service("purchaseReqBillService")
public class PurchaseReqBillServiceImpl extends
		BillBaseServiceImpl<PurchaseReqBillInfo, PurchaseReqBillCriteria, PurchaseReqBillDao> implements
		PurchaseReqBillService {
	@Autowired
	private PurchaseReqEntryDao purchaseReqEntryDao;
	@Autowired
	private PurchaseReqEntryService purchaseReqEntryService;

	@Override
	public void delete(PurchaseReqBillInfo info) {
		super.delete(info);
		purchaseReqEntryDao.deleteByPurchaseReqBill(info);
	}

	@Override
	public void addNew(PurchaseReqBillInfo info) {
		super.addNew(info);
		_addEntry(info);
	}

	@Override
	public void save(PurchaseReqBillInfo info) {
		super.save(info);
		_addEntry(info);
	}

	private void _addEntry(PurchaseReqBillInfo info) {
		purchaseReqEntryDao.deleteByPurchaseReqBill(info);
		for (PurchaseReqEntryInfo entry : info.getEntry()) {
			purchaseReqEntryService.addNew(entry);
		}
	}
}
