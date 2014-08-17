package com.minyisoft.webapp.yjmz.common.service.impl;

import org.springframework.stereotype.Service;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.model.criteria.UserOrgRelationCriteria;
import com.minyisoft.webapp.yjmz.common.model.UserOrgRelationInfo;
import com.minyisoft.webapp.yjmz.common.persistence.UserOrgRelationDao;
import com.minyisoft.webapp.yjmz.common.service.UserOrgRelationService;

@Service("userOrgRelationService")
public class UserOrgRelationServiceImpl extends BaseServiceImpl<UserOrgRelationInfo,UserOrgRelationCriteria,UserOrgRelationDao> implements UserOrgRelationService {
}
