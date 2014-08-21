package com.minyisoft.webapp.yjmz.oa.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.google.common.base.Optional;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.UserOrgRelationInfo;
import com.minyisoft.webapp.yjmz.common.security.SecurityUtils;
import com.minyisoft.webapp.yjmz.oa.model.ReportInfo;
import com.minyisoft.webapp.yjmz.oa.model.criteria.ReportCriteria;
import com.minyisoft.webapp.yjmz.oa.persistence.ReportDao;
import com.minyisoft.webapp.yjmz.oa.service.ReportService;

@Service("reportService")
public class ReportServiceImpl extends BaseServiceImpl<ReportInfo, ReportCriteria, ReportDao> implements ReportService {
	@Override
	protected void _validateDataBeforeDelete(ReportInfo info) {
		Assert.isTrue(info.getCreateUser() != null && info.getCreateUser().equals(SecurityUtils.getCurrentUser()),
				"非报告创建者不允许删除工作报告");
		Assert.isTrue(StringUtils.isBlank(info.getProcessInstanceId()), "不允许删除已提交审批流程的工作报告");
	}

	@Override
	protected void _validateDataBeforeAdd(ReportInfo info) {
		UserInfo createUser = (info.getCreateUser() == null) ? SecurityUtils.getCurrentUser() : (UserInfo) info
				.getCreateUser();
		Assert.isTrue(createUser.isBelongTo(info.getCompany()), "工作报告创建者并不隶属于报告所属公司");
		if (info.getDepartment() != null) {
			Optional<UserOrgRelationInfo> orgRelation = createUser.getOrgRelation(info.getCompany());
			Assert.isTrue(orgRelation.isPresent() && info.getDepartment().equals(orgRelation.get().getDepartment()),
					"工作报告创建者并不隶属于报告所属部门");
		}
	}
}
