package com.minyisoft.webapp.yjmz.oa.service.impl;

import org.springframework.stereotype.Service;

import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.oa.model.PersonnelChangeBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.criteria.PersonnelChangeBillCriteria;
import com.minyisoft.webapp.yjmz.oa.model.enumField.PersonnelChangeTypeEnum;
import com.minyisoft.webapp.yjmz.oa.persistence.PersonnelChangeBillDao;
import com.minyisoft.webapp.yjmz.oa.service.PersonnelChangeBillService;

@Service("personnelChangeBillService")
public class PersonnelChangeBillServiceImpl extends
		BaseServiceImpl<PersonnelChangeBillInfo, PersonnelChangeBillCriteria, PersonnelChangeBillDao> implements
		PersonnelChangeBillService {
	@Override
	protected void _validateDataBeforeSubmit(PersonnelChangeBillInfo info) {
		if (info.getChangeType() != PersonnelChangeTypeEnum.OTHER) {
			info.setOtherChangeType(null);
		}
	}
}
