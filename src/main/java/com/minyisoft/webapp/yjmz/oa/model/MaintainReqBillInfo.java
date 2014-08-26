package com.minyisoft.webapp.yjmz.oa.model;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.yjmz.common.model.CompanyBillBaseInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;
import com.minyisoft.webapp.yjmz.oa.model.enumField.MaintainTypeEnum;

/**
 * @author qingyong_ou 工程维修单
 */
@Label("工程维修单")
@Getter
@Setter
@ModelKey(0x147E24CEB79L)
public class MaintainReqBillInfo extends CompanyBillBaseInfo implements WorkFlowBusinessModel {
	@Label("位置")
	@NotNull
	private String location;
	@Label("维修类型")
	@NotNull
	private MaintainTypeEnum[] maintainTypes;
	// 维修技师
	private UserInfo maintenanceMan;
	// 完成日期
	private Date finishDate;
	// 维修材料分录
	private List<MaintainReqEntryInfo> entry;
	// 接单人
	private UserInfo receiver;
	// 验收人
	private UserInfo examiner;
	// 工作流流程实例id
	private String processInstanceId;

	@Override
	public String getProcessInstanceName() {
		StringBuffer sb = new StringBuffer(getCompany().getName());
		if (getDepartment() != null) {
			sb.append(getDepartment().getName());
		}
		return sb.append("工程维修单[").append(getBillNumber()).append("]").toString();
	}

	@Override
	public boolean isProcessUnStarted() {
		return StringUtils.isBlank(processInstanceId);
	}
}
