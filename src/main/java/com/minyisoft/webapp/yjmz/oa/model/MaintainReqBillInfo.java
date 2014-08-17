package com.minyisoft.webapp.yjmz.oa.model;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.core.model.BillBaseInfo;
import com.minyisoft.webapp.yjmz.common.model.DepartmentInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.oa.model.enumField.MaintainTypeEnum;

/**
 * @author qingyong_ou 工程维修单
 */
@Getter
@Setter
@ModelKey(0x147E24CEB79L)
public class MaintainReqBillInfo extends BillBaseInfo {
	@Label("申请部门")
	@NotNull
	private DepartmentInfo applyDepartment;
	@Label("申请人")
	@NotNull
	private UserInfo applyUser;
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
	@Label("维修材料分录")
	@Min(1)
	private List<MaintainReqEntryInfo> entry;
	// 接单人
	private UserInfo receiver;
	// 验收人
	private UserInfo examiner;
}
