package com.minyisoft.webapp.yjmz.oa.model;

import static org.springframework.util.Assert.notNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Optional;
import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.yjmz.common.model.CompanyWorkFlowBillBaseInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.oa.model.enumField.MaintainReplyEnum;
import com.minyisoft.webapp.yjmz.oa.model.enumField.MaintainTypeEnum;

/**
 * @author qingyong_ou 工程维修单
 */
@Label("工程维修单")
@Getter
@Setter
@ModelKey(0x147E24CEB79L)
public class MaintainReqBillInfo extends CompanyWorkFlowBillBaseInfo {
	@Label("位置")
	@NotNull
	private String location;
	@Label("维修类型")
	@NotNull
	private MaintainTypeEnum[] maintainTypes;
	// 维修技师
	private UserInfo maintenanceMan;
	// 完成日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date finishDate;
	// 维修答复
	private MaintainReplyEnum maintainReply;
	// 维修材料分录
	private List<MaintainReqEntryInfo> entry;
	// 接单人
	private UserInfo receiver;
	// 验收人
	private UserInfo examiner;

	/**
	 * 获取维修材料总数量
	 * 
	 * @return
	 */
	public Optional<BigDecimal> getMaterialsTotalQuantity() {
		if (!CollectionUtils.isEmpty(entry)) {
			BigDecimal total = BigDecimal.ZERO;
			for (MaintainReqEntryInfo e : entry) {
				total = total.add(e.getQuantity());
			}
			return Optional.of(total);
		}
		return Optional.absent();
	}

	/**
	 * 获取维修材料总金额
	 * 
	 * @return
	 */
	public Optional<BigDecimal> getMaterialsTotalPrice() {
		if (!CollectionUtils.isEmpty(entry)) {
			BigDecimal total = BigDecimal.ZERO;
			for (MaintainReqEntryInfo e : entry) {
				total = total.add(e.getPrice());
			}
			return Optional.of(total);
		}
		return Optional.absent();
	}

	@Override
	public String getProcessInstanceName() {
		StringBuffer sb = new StringBuffer(getCompany().getName());
		if (getDepartment() != null) {
			sb.append(getDepartment().getName());
		}
		return sb.append("工程维修单").toString();
	}

	private static final String PROCESS_VARIABLE_NAME = "maintainReqBill";

	@Override
	public String getBusinessModelProcessVariableName() {
		return PROCESS_VARIABLE_NAME;
	}

	private static final String _PURCHASE_CREATE_PREMISSION = "MaintainReqBill:create";

	/**
	 * 获取维修单申请部门采购单发起人
	 * 
	 * @return
	 */
	public UserInfo getApplyDeptDeptPurchasePromoter() {
		notNull(getCreateUser());
		return ((UserInfo) getCreateUser()).hasPermission(getCompany(), _PURCHASE_CREATE_PREMISSION) ? ((UserInfo) getCreateUser())
				: ((UserInfo) getCreateUser()).getUpperUser(getCompany());
	}
}
