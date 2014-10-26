package com.minyisoft.webapp.yjmz.oa.model.entity;

import java.util.Date;

import com.minyisoft.webapp.yjmz.common.model.UserInfo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author qingyong_ou 采购进度报告
 */
@Getter
@Setter
public class PurchaseProcessReportEntity {
	// 报告内容
	private String reportDetail;
	// 报告人
	private UserInfo reportUser;
	// 报告时间
	private Date reportDate;
}
