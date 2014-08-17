package com.minyisoft.webapp.yjmz.common.persistence;

import com.minyisoft.webapp.core.persistence.BaseDao;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.UserCriteria;

public interface UserDao extends BaseDao<UserInfo, UserCriteria> {
	/**
	 * 累加用户登录次数
	 * 
	 * @param user
	 * @return
	 */
	int increaseUserLoginCount(UserInfo user);

	/**
	 * 添加用户所在组织（公司或富星后台管理员）信息
	 * 
	 * @param orgRelation
	 */
	//int insertUserOrganization(UserOrgRelationInfo orgRelation);

	/**
	 * 删除用户所在组织架构信息
	 * 
	 * @param user
	 * @param org
	 * @return
	 */
	//int deleteUserOrganization(@Param("user") UserInfo user, @Param("org") ISystemOrgObject org);

	/**
	 * 删除指定用户所在组织信息后，更新相应用户上下级关系
	 * 
	 * @param deleteUser
	 * @param newUpperUser
	 * @param org
	 * @return
	 */
	//int updateUserPathAfterUserOrgDelete(@Param("deleteUser") UserInfo deleteUser,
			//@Param("newUpperUser") UserInfo newUpperUser, @Param("org") ISystemOrgObject org);

	/**
	 * 删除指定组织架构的所有用户信息
	 * 
	 * @param org
	 * @return
	 */
	//int deleteOrganizationUsers(@Param("org") ISystemOrgObject org);

	/**
	 * 清空指定微信用户id
	 * 
	 * @param weixinOpenId
	 * @return
	 */
	int clearWeixinOpenId(String weixinOpenId);
}
