package com.minyisoft.webapp.yjmz.common.model.entity;

import lombok.Getter;

/**
 * @author qingyong_ou 微信模板消息
 */
@Getter
public enum WeixinTemplateMessage {
	ORDER_STATUS_NOTIFY("vgzewxKFp128Jw1amIGyu6pemD-Yjq6ywiCYRY6vKxs", "first", "keyword1", "keyword2", "keyword3",
			"remark"), // 订单状态提醒
	PENDING_TRANSACTION_NOTIFY("wGtvE3gTEr4CT_-sZAU6ADrjpkRQjkMbbegciKEZ2lA", "first", "keyword1", "keyword2",
			"keyword3", "keyword4", "remark");// 待处理事务通知

	private String templateId;
	private String[] placeholders;

	private WeixinTemplateMessage(String templateId, String... params) {
		this.templateId = templateId;
		this.placeholders = params;
	}
}
