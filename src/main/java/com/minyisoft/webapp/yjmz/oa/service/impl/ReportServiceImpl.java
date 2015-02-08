package com.minyisoft.webapp.yjmz.oa.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.google.common.base.Optional;
import com.minyisoft.webapp.core.exception.ServiceException;
import com.minyisoft.webapp.core.model.IBillObject;
import com.minyisoft.webapp.core.service.impl.BillBaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.UserOrgRelationInfo;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowProcessStatusEnum;
import com.minyisoft.webapp.yjmz.common.security.SecurityUtils;
import com.minyisoft.webapp.yjmz.common.service.UserOrgRelationService;
import com.minyisoft.webapp.yjmz.oa.model.AcceptanceBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.ReportInfo;
import com.minyisoft.webapp.yjmz.oa.model.criteria.ReportCriteria;
import com.minyisoft.webapp.yjmz.oa.model.enumField.AcceptanceStatusEnum;
import com.minyisoft.webapp.yjmz.oa.persistence.ReportDao;
import com.minyisoft.webapp.yjmz.oa.service.ReportService;

@Service("reportService")
public class ReportServiceImpl extends BillBaseServiceImpl<ReportInfo, ReportCriteria, ReportDao> implements
		ReportService {
	@Autowired
	private UserOrgRelationService userOrgRelationService;

	@Override
	protected void _validateDataBeforeDelete(ReportInfo info) {
		Assert.isTrue(info.getCreateUser() != null && info.getCreateUser().equals(SecurityUtils.getCurrentUser()),
				"当前用户并非工作报告创建者，不允许删除报告");
		Assert.isTrue(info.isProcessUnStarted(), "不允许删除已提交审批流程的工作报告");
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

	@Override
	protected void _validateDataBeforeSubmit(ReportInfo info) {
		// 设置了前置审批部门，自动置入对应的部门负责人
		if (info.getPreApproveDepartment() != null) {
			info.setPreApproveDepartmentLeader(userOrgRelationService.getDepartmentLeader(
					info.getPreApproveDepartment()).orNull());
		} else {
			info.setPreApproveDepartmentLeader(null);
		}
	}

	@Override
	protected boolean useModelCache() {
		return true;
	}

	@Override
	public void processAfterTargetBillAdded(ReportInfo sourceBill, IBillObject targetBill) {
		if (targetBill instanceof AcceptanceBillInfo) {
			if (sourceBill.getProcessStatus() == WorkFlowProcessStatusEnum.FINISHED
					&& sourceBill.getAcceptanceStatus() == AcceptanceStatusEnum.UNCOMMITTED) {
				sourceBill.setAcceptanceStatus(AcceptanceStatusEnum.RUNNING);
				save(sourceBill);
			} else {
				throw new ServiceException(sourceBill.getProcessInstanceName() + "无需验收");
			}
		}
	}

	@Override
	public void processAfterTargetBillUpdated(ReportInfo sourceBill, IBillObject targetBill) {
		if (targetBill instanceof AcceptanceBillInfo
				&& ((AcceptanceBillInfo) targetBill).getProcessStatus() == WorkFlowProcessStatusEnum.FINISHED) {
			sourceBill.setAcceptanceStatus(AcceptanceStatusEnum.FINISHED);
			save(sourceBill);
		}
	}
}
