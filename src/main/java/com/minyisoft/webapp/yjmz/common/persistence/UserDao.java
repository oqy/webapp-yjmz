package com.minyisoft.webapp.yjmz.common.persistence;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;

import com.minyisoft.webapp.core.persistence.BaseDao;
import com.minyisoft.webapp.core.utils.spring.cache.ModelObjectCacheManager;
import com.minyisoft.webapp.core.utils.spring.cache.ModelObjectCacheType;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.UserCriteria;

@ModelObjectCacheType(modelType = UserInfo.class)
@CacheConfig(cacheNames = ModelObjectCacheManager.DUMMY_CACHE)
public interface UserDao extends BaseDao<UserInfo, UserCriteria> {
	/**
	 * 累加用户登录次数
	 * 
	 * @param user
	 * @return
	 */
	@CacheEvict(key = "#p0.id")
	int increaseUserLoginCount(UserInfo user);

	/**
	 * 清空指定微信用户id
	 * 
	 * @param weixinOpenId
	 * @return
	 */
	@CacheEvict(allEntries = true)
	int clearWeixinOpenId(String weixinOpenId);
}
