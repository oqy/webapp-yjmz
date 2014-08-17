package com.minyisoft.webapp.yjmz.oa.model;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.core.model.BillBaseInfo;
import com.minyisoft.webapp.core.model.assistant.ISeqCodeGenStrategy;
import com.minyisoft.webapp.yjmz.common.model.DepartmentInfo;

/**
 * @author qingyong_ou 报告
 */
@Getter
@Setter
@ModelKey(0x147E1C6843FL)
public class ReportInfo extends BillBaseInfo {
	@Label("所属部门")
	@NotNull
	private DepartmentInfo department;
	// 档案编号
	private String fileNumber;
	@Label("报告标题")
	@NotBlank
	private String reportTitle;
	// 附件url
	private String attachmentUrl;

	@Override
	public ISeqCodeGenStrategy getSeqCodeGenStrategy() {
		return null;
	}

}
