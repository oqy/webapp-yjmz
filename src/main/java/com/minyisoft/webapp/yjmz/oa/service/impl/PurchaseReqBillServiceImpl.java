package com.minyisoft.webapp.yjmz.oa.service.impl;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.minyisoft.webapp.core.exception.ServiceException;
import com.minyisoft.webapp.core.service.impl.BillBaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.security.SecurityUtils;
import com.minyisoft.webapp.yjmz.oa.model.PurchaseReqBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.PurchaseReqEntryInfo;
import com.minyisoft.webapp.yjmz.oa.model.criteria.PurchaseReqBillCriteria;
import com.minyisoft.webapp.yjmz.oa.model.entity.PurchaseProcessReportEntity;
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
		Assert.isTrue(info.isProcessUnStarted(), "不允许删除已提交审批流程的采购单");
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
		Assert.isTrue(!CollectionUtils.isEmpty(info.getEntry()), "采购单采购分录不能为空");
		int entryCount = 0;
		for (PurchaseReqEntryInfo entry : info.getEntry()) {
			if (StringUtils.isBlank(entry.getName())) {
				continue;
			}
			entry.setReqBill(info);
			purchaseReqEntryService.submit(entry);
			entryCount++;
		}
		if (entryCount == 0) {
			throw new ServiceException("采购单分类不能为空");
		}
		purchaseReqEntryDao.deleteInvalidEntryByPurchaseReqBill(info);
	}

	@Override
	protected boolean useModelCache() {
		return true;
	}

	@Override
	public void addProcessReport(PurchaseReqBillInfo reqBill, boolean purchaseFinished, String reportDetail) {
		Assert.notNull(reqBill);
		UserInfo currentUser = null;
		if (!purchaseFinished && StringUtils.isNotBlank(reportDetail)
				&& (currentUser = SecurityUtils.getCurrentUser()) != null) {
			PurchaseProcessReportEntity processReport = new PurchaseProcessReportEntity();
			processReport.setReportDate(new Date());
			processReport.setReportDetail(reportDetail);
			processReport.setReportUser(currentUser);
			getBaseDao().addProcessReport(reqBill, processReport);
		}
	}
}
