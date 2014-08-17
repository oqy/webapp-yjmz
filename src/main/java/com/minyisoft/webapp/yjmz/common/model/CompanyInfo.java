package com.minyisoft.webapp.yjmz.common.model;

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.core.model.DataBaseInfo;
import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.yjmz.common.model.enumField.CompanyStatusEnum;

/**
 * @author qingyong_ou 公司信息
 * 
 */
@Getter
@Setter
@ModelKey(0x147C41F1226L)
public class CompanyInfo extends DataBaseInfo implements ISystemOrgObject {
	@Label("公司状态")
	@NotNull
	private CompanyStatusEnum status = CompanyStatusEnum.NORMAL;
	// 包含部门
	private List<DepartmentInfo> departments = Collections.emptyList();
}
