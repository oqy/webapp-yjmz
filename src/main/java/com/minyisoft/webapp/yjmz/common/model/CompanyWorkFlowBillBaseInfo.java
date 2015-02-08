package com.minyisoft.webapp.yjmz.common.model;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowProcessStatusEnum;

/**
 * @author qingyong_ou
 * 
 */
@Getter
@Setter
public abstract class CompanyWorkFlowBillBaseInfo extends CompanyBillBaseInfo implements WorkFlowBusinessModel {
	// 工作流流程实例id
	private String processInstanceId;
	// 工作流程状态
	private WorkFlowProcessStatusEnum processStatus = WorkFlowProcessStatusEnum.UNSTARTED;
	// 工作流程创建时间
	private Date processBeginDate;
	// 工作流程结束时间
	private Date processEndDate;

	@Override
	public boolean isProcessUnStarted() {
		return StringUtils.isBlank(processInstanceId);
	}
}
