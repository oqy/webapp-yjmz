package com.minyisoft.webapp.yjmz.oa.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minyisoft.webapp.core.service.impl.BillBaseServiceImpl;
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
	public void delete(MaintainReqBillInfo info) {
		super.delete(info);
		maintainReqEntryDao.deleteByMaintainReqBill(info);
	}

	@Override
	public void addNew(MaintainReqBillInfo info) {
		super.addNew(info);
		_addEntry(info);
	}

	@Override
	public void save(MaintainReqBillInfo info) {
		super.save(info);
		_addEntry(info);
	}

	private void _addEntry(MaintainReqBillInfo info) {
		maintainReqEntryDao.deleteByMaintainReqBill(info);
		for (MaintainReqEntryInfo entry : info.getEntry()) {
			maintainReqEntryService.addNew(entry);
		}
	}
}
