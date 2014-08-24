package com.minyisoft.webapp.yjmz.oa.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.minyisoft.webapp.core.service.impl.BillBaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.security.SecurityUtils;
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
	protected void _validateDataBeforeDelete(PurchaseReqBillInfo info) {
		Assert.isTrue(info.getCreateUser() != null && info.getCreateUser().equals(SecurityUtils.getCurrentUser()),
				"当前用户并非采购单创建者，不允许删除采购单");
		Assert.isTrue(info.isProcessUnStarted(), "不允许删除已提交审批流程的工作报告");
	}

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
			if (StringUtils.isBlank(entry.getName())) {
				continue;
			}
			entry.setReqBill(info);
			purchaseReqEntryService.addNew(entry);
		}
	}
}
