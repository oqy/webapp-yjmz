package com.minyisoft.webapp.yjmz.oa.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.minyisoft.webapp.core.service.impl.BillBaseServiceImpl;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowProcessStatusEnum;
import com.minyisoft.webapp.yjmz.common.security.SecurityUtils;
import com.minyisoft.webapp.yjmz.common.service.WorkFlowProcessService;
import com.minyisoft.webapp.yjmz.oa.model.AcceptanceBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.criteria.AcceptanceBillCriteria;
import com.minyisoft.webapp.yjmz.oa.persistence.AcceptanceBillDao;
import com.minyisoft.webapp.yjmz.oa.service.AcceptanceBillService;

@Service("acceptanceBillService")
public class AcceptanceBillServiceImpl extends
		BillBaseServiceImpl<AcceptanceBillInfo, AcceptanceBillCriteria, AcceptanceBillDao> implements
		AcceptanceBillService {
	@Autowired
	private WorkFlowProcessService workFlowProcessService;

	@Override
	protected void _validateDataBeforeAdd(AcceptanceBillInfo info) {
		if (info.getSourceBill() instanceof WorkFlowBusinessModel) {
			Assert.isTrue(
					((WorkFlowBusinessModel) info.getSourceBill()).getProcessStatus() == WorkFlowProcessStatusEnum.FINISHED,
					"未完成审批流程的" + ObjectUuidUtils.getClassLabel(info.getClass()) + "不允许提请验收");
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
