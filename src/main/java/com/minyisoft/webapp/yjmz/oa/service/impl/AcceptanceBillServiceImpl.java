package com.minyisoft.webapp.yjmz.oa.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;
import com.minyisoft.webapp.yjmz.common.security.SecurityUtils;
import com.minyisoft.webapp.yjmz.common.service.WorkFlowProcessService;
import com.minyisoft.webapp.yjmz.oa.model.AcceptanceBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.criteria.AcceptanceBillCriteria;
import com.minyisoft.webapp.yjmz.oa.persistence.AcceptanceBillDao;
import com.minyisoft.webapp.yjmz.oa.service.AcceptanceBillService;

@Service("acceptanceBillService")
public class AcceptanceBillServiceImpl extends
		BaseServiceImpl<AcceptanceBillInfo, AcceptanceBillCriteria, AcceptanceBillDao> implements AcceptanceBillService {
	@Autowired
	private WorkFlowProcessService workFlowProcessService;

	@Override
	protected void _validateDataBeforeAdd(AcceptanceBillInfo info) {
		if (info.getSourceBill() instanceof WorkFlowBusinessModel) {
			Assert.isTrue(
					((WorkFlowBusinessModel) info.getSourceBill()).getCreateUser().equals(
							SecurityUtils.getCurrentUser()), "需由原流程发起人提出验收申请");
		}
	}

	@Override
	public void addNew(AcceptanceBillInfo info) {
		super.addNew(info);
		workFlowProcessService.startProcess(info);
	}
}
