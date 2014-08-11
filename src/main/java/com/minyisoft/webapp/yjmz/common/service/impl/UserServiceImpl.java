package com.minyisoft.webapp.yjmz.common.service.impl;

import org.springframework.stereotype.Service;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.model.criteria.UserCriteria;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.persistence.UserDao;
import com.minyisoft.webapp.yjmz.common.service.UserService;

@Service("userService")
public class UserServiceImpl extends BaseServiceImpl<UserInfo,UserCriteria,UserDao> implements UserService {
}
