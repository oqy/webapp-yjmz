package com.minyisoft.webapp.yjmz.oa.model.criteria;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.CompanyBaseCriteria;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowProcessStatusEnum;

@Getter
@Setter
public class ReportCriteria extends CompanyBaseCriteria {
	// 报告浏览人
	private UserInfo viewer;
	@Label("流程状态")
	private WorkFlowProcessStatusEnum processStatus;
}
