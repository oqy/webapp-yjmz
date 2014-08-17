package com.minyisoft.webapp.yjmz.common.persistence;

import com.minyisoft.webapp.core.persistence.BaseDao;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.UserOrgRelationInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.UserOrgRelationCriteria;

public interface UserOrgRelationDao extends BaseDao<UserOrgRelationInfo, UserOrgRelationCriteria> {
	/**
	 * 删除指定用户所有组织关系信息
	 * 
	 * @param user
	 * @return
	 */
	int deleteRelations(UserInfo user);
}
