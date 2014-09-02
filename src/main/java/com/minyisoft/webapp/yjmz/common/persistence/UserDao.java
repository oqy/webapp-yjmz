package com.minyisoft.webapp.yjmz.common.persistence;

import com.minyisoft.webapp.core.persistence.BaseDao;
import com.minyisoft.webapp.core.utils.spring.cache.annotation.ModelCacheEvict;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.UserCriteria;

public interface UserDao extends BaseDao<UserInfo, UserCriteria> {
	/**
	 * 累加用户登录次数
	 * 
	 * @param user
	 * @return
	 */
	@ModelCacheEvict(modelType = UserInfo.class, key = "#p0.id")
	int increaseUserLoginCount(UserInfo user);

	/**
	 * 清空指定微信用户id
	 * 
	 * @param weixinOpenId
	 * @return
	 */
	@ModelCacheEvict(modelType = UserInfo.class, allEntries = true)
	int clearWeixinOpenId(String weixinOpenId);
}
