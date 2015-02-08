package com.minyisoft.webapp.yjmz.oa.model;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotBlank;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.core.model.EntryBaseInfo;

/**
 * @author qingyong_ou 采购单分录
 */
@Getter
@Setter
@ModelKey(0x147E34CAA0AL)
public class PurchaseReqEntryInfo extends EntryBaseInfo {
	@Label("采购单")
	@NotNull
	private PurchaseReqBillInfo reqBill;
	@Label("品名")
	@NotBlank
	private String name;
	// 规格
	private String standard;
	@Label("申请采购量")
	@NotNull
	private BigDecimal quantity = BigDecimal.ZERO;
	// 单价
	private BigDecimal unitPrice = BigDecimal.ZERO;
	// 备注
	private String remark;
	// 已有仓储量
	private BigDecimal depotAmount = BigDecimal.ZERO;
	// 实际采购量
	private BigDecimal actualQuantity = BigDecimal.ZERO;
}
