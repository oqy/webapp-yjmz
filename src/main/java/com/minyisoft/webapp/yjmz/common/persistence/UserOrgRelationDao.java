package com.minyisoft.webapp.yjmz.common.persistence;

import org.apache.ibatis.annotations.Param;

import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.persistence.BaseDao;
import com.minyisoft.webapp.yjmz.common.model.DepartmentInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.UserOrgRelationInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.UserOrgRelationCriteria;

public interface UserOrgRelationDao extends BaseDao<UserOrgRelationInfo, UserOrgRelationCriteria> {
	/**
	 * 删除指定用户指定组织的组织关系信息
	 * 
	 * @param user
	 * @param org
	 * @return
	 */
	int deleteRelation(@Param("user") UserInfo user, @Param("org") ISystemOrgObject org);

	/**
	 * 用指定的新部门替换指定的旧部门
	 * 
	 * @param oldDepartment
	 * @param newDepartment
	 * @return
	 */
	int replaceDepartment(@Param("oldDepartment") DepartmentInfo oldDepartment,
			@Param("newDepartment") DepartmentInfo newDepartment);
}
