package com.minyisoft.webapp.yjmz.oa.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.minyisoft.webapp.core.service.impl.BillBaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.security.SecurityUtils;
import com.minyisoft.webapp.yjmz.oa.model.PersonnelChangeBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.criteria.PersonnelChangeBillCriteria;
import com.minyisoft.webapp.yjmz.oa.model.enumField.PersonnelChangeTypeEnum;
import com.minyisoft.webapp.yjmz.oa.persistence.PersonnelChangeBillDao;
import com.minyisoft.webapp.yjmz.oa.service.PersonnelChangeBillService;

@Service("personnelChangeBillService")
public class PersonnelChangeBillServiceImpl extends
		BillBaseServiceImpl<PersonnelChangeBillInfo, PersonnelChangeBillCriteria, PersonnelChangeBillDao> implements
		PersonnelChangeBillService {
	@Override
	protected void _validateDataBeforeSubmit(PersonnelChangeBillInfo info) {
		if (info.getChangeType() != PersonnelChangeTypeEnum.OTHER) {
			info.setOtherChangeType(null);
		}
	}

	@Override
	protected void _validateDataBeforeDelete(PersonnelChangeBillInfo info) {
		Assert.isTrue(info.getCreateUser() != null && info.getCreateUser().equals(SecurityUtils.getCurrentUser()),
				"当前用户并非人事变动单创建者，不允许删除人事变动单");
		Assert.isTrue(info.isProcessUnStarted(), "不允许删除已提交审批流程的人事变动单");
	}
}
