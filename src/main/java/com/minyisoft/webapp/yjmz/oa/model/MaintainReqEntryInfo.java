package com.minyisoft.webapp.yjmz.oa.model;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.core.model.EntryBaseInfo;

/**
 * @author qingyong_ou 工程维修单分录
 */
@Getter
@Setter
@ModelKey(0x147E2588E3BL)
public class MaintainReqEntryInfo extends EntryBaseInfo {
	@Label("维修单")
	@NotNull
	private MaintainReqBillInfo reqBill;
	@Label("维修材料")
	@NotBlank
	private String name;
	// 数量
	private BigDecimal quantity = BigDecimal.ZERO;
	// 金额
	private BigDecimal price = BigDecimal.ZERO;
}
