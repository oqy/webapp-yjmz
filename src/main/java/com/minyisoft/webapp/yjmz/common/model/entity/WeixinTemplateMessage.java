package com.minyisoft.webapp.yjmz.common.model.entity;

import lombok.Getter;

/**
 * @author qingyong_ou 微信模板消息
 */
@Getter
public enum WeixinTemplateMessage {
	ORDER_STATUS_NOTIFY("vgzewxKFp128Jw1amIGyu6pemD-Yjq6ywiCYRY6vKxs", "first", "keyword1", "keyword2", "keyword3",
			"remark");// 订单状态提醒

	private String templateId;
	private String[] params;

	private WeixinTemplateMessage(String templateId, String... params) {
		this.templateId = templateId;
		this.params = params;
	}
}
