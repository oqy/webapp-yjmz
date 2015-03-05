package com.minyisoft.webapp.yjmz.common.service;

import com.minyisoft.webapp.core.service.BaseService;
import com.minyisoft.webapp.yjmz.common.model.MessageInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.MessageCriteria;
import com.minyisoft.webapp.yjmz.common.model.entity.WeixinTemplateMessage;

public interface MessageService extends BaseService<MessageInfo, MessageCriteria> {
	/**
	 * 标记指定接收人已读指定消息
	 * 
	 * @param message
	 * @param receiver
	 */
	void markRead(MessageInfo message, UserInfo receiver);

	/**
	 * 向指定微信openId发送模板消息
	 * 
	 * @param weixinOpenId
	 * @param templateMessage
	 * @param url
	 * @param params
	 */
	void sendWeixinTemplateMessage(String weixinOpenId, WeixinTemplateMessage templateMessage, String url,
			String... params);
}
