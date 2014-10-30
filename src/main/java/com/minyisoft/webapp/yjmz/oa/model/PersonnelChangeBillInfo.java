package com.minyisoft.webapp.yjmz.oa.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.yjmz.common.model.CompanyWorkFlowBillBaseInfo;
import com.minyisoft.webapp.yjmz.common.model.DepartmentInfo;
import com.minyisoft.webapp.yjmz.common.model.enumField.UserMaleEnum;
import com.minyisoft.webapp.yjmz.oa.model.enumField.PersonnelChangeTypeEnum;

@Label("人事变动单")
@Getter
@Setter
@ModelKey(0x14920E2FE89L)
public class PersonnelChangeBillInfo extends CompanyWorkFlowBillBaseInfo {
	@Label("姓名")
	@NotBlank
	private String staffName;
	@Label("性别")
	@NotNull
	private UserMaleEnum userMale = UserMaleEnum.MALE;
	// 员工号码
	private String staffNumber;
	@Label("入职日期")
	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date entryDate;
	@Label("生效日期")
	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date effectiveDate;
	@Label("变动类型")
	@NotNull
	private PersonnelChangeTypeEnum changeType = PersonnelChangeTypeEnum.PROBATION_FINISH;
	// 其他变动类型
	private String otherChangeType;
	// 原职位
	private String oriPosition;
	// 原级别
	private String oriRank;
	// 原工资
	private BigDecimal oriSalary = BigDecimal.ZERO;
	// 新部门
	private DepartmentInfo newDepartment;
	// 新职位
	private String newPosition;
	// 新级别
	private String newRank;
	// 新工资
	private BigDecimal newSalary = BigDecimal.ZERO;
	// 最后工作日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date lastWorkDate;
	// 工资变动原因
	private String salaryChangeReason;

	public String getChangeTypeDescription() {
		return changeType == PersonnelChangeTypeEnum.OTHER ? otherChangeType : changeType.getDescription();
	}

	public BigDecimal getSalaryChangeAmount() {
		return newSalary != null && oriSalary != null ? newSalary.subtract(oriSalary) : BigDecimal.ZERO;
	}

	@Override
	public String getProcessInstanceName() {
		StringBuffer sb = new StringBuffer(getCompany().getName());
		if (getDepartment() != null) {
			sb.append(getDepartment().getName());
		}
		return sb.append("人事变动单").toString();
	}

	private static final String PROCESS_VARIABLE_NAME = "personnelChangeBill";

	@Override
	public String getBusinessModelProcessVariableName() {
		return PROCESS_VARIABLE_NAME;
	}
}
