package com.minyisoft.webapp.yjmz.oa.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.minyisoft.webapp.core.service.impl.BillBaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.security.SecurityUtils;
import com.minyisoft.webapp.yjmz.oa.model.MaintainReqBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.MaintainReqEntryInfo;
import com.minyisoft.webapp.yjmz.oa.model.criteria.MaintainReqBillCriteria;
import com.minyisoft.webapp.yjmz.oa.persistence.MaintainReqBillDao;
import com.minyisoft.webapp.yjmz.oa.persistence.MaintainReqEntryDao;
import com.minyisoft.webapp.yjmz.oa.service.MaintainReqBillService;
import com.minyisoft.webapp.yjmz.oa.service.MaintainReqEntryService;

@Service("maintainReqBillService")
public class MaintainReqBillServiceImpl extends
		BillBaseServiceImpl<MaintainReqBillInfo, MaintainReqBillCriteria, MaintainReqBillDao> implements
		MaintainReqBillService {
	@Autowired
	private MaintainReqEntryDao maintainReqEntryDao;
	@Autowired
	private MaintainReqEntryService maintainReqEntryService;

	@Override
	protected void _validateDataBeforeDelete(MaintainReqBillInfo info) {
		Assert.isTrue(info.getCreateUser() != null && info.getCreateUser().equals(SecurityUtils.getCurrentUser()),
				"当前用户并非工程维修单创建者，不允许删除维修单");
		Assert.isTrue(info.isProcessUnStarted(), "不允许删除已提交审批流程的工作报告");
	}

	@Override
	public void delete(MaintainReqBillInfo info) {
		super.delete(info);
		maintainReqEntryDao.deleteByMaintainReqBill(info);
	}

	@Override
	public void save(MaintainReqBillInfo info) {
		super.save(info);
		for (MaintainReqEntryInfo entry : info.getEntry()) {
			if (StringUtils.isEmpty(entry.getName())) {
				continue;
			}
			entry.setReqBill(info);
			maintainReqEntryService.submit(entry);
		}
		maintainReqEntryDao.deleteInvalidEntryByMaintainReqBill(info);
	}
	
	@Override
	protected boolean useModelCache() {
		return true;
	}
}
