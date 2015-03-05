package com.minyisoft.webapp.yjmz.common.service.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.google.common.collect.Maps;
import com.minyisoft.webapp.core.exception.ServiceException;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.weixin.mp.dto.MpDevCredential;
import com.minyisoft.webapp.weixin.mp.dto.MpEnvelope;
import com.minyisoft.webapp.weixin.mp.dto.message.send.TemplateMessageData;
import com.minyisoft.webapp.weixin.mp.service.MpPostService;
import com.minyisoft.webapp.yjmz.common.model.MessageInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.MessageCriteria;
import com.minyisoft.webapp.yjmz.common.model.entity.MessageReceiveEntity;
import com.minyisoft.webapp.yjmz.common.model.entity.WeixinTemplateMessage;
import com.minyisoft.webapp.yjmz.common.persistence.MessageDao;
import com.minyisoft.webapp.yjmz.common.service.MessageService;
import com.minyisoft.webapp.yjmz.weixin.web.interceptor.WeixinOAuthInterceptor;

@Service("messageService")
public class MessageServiceImpl extends BaseServiceImpl<MessageInfo, MessageCriteria, MessageDao> implements
		MessageService {
	@Autowired
	private MpPostService mpPostService;
	@Autowired
	private MpDevCredential mpDevCredential;

	@Override
	public void delete(MessageInfo info) {
		throw new ServiceException("不允许删除系统私信");
	}

	@Override
	public void save(MessageInfo info) {
		throw new ServiceException("不允许更新系统私信");
	}

	@Override
	public void addNew(MessageInfo info) {
		super.addNew(info);
		for (MessageReceiveEntity entity : info.getReceiveTargets()) {
			Assert.isTrue(entity != null && entity.getReceiver() != null, "私信接收人不能为空");
			getBaseDao().addMessageReceiver(info, entity.getReceiver());
		}
	}

	@Override
	public void markRead(MessageInfo message, UserInfo receiver) {
		Assert.notNull(message);
		Assert.notNull(receiver);
		getBaseDao().updateMessageReadDate(message, receiver);
	}

	@Override
	public void sendWeixinTemplateMessage(String weixinOpenId, WeixinTemplateMessage templateMessage, String url,
			String... params) {
		if (StringUtils.isNotBlank(weixinOpenId)
				&& templateMessage != null
				&& (templateMessage.getPlaceholders() == null || (params != null && params.length == templateMessage
						.getPlaceholders().length))) {
			Map<String, TemplateMessageData> messageData = Maps.newHashMap();
			for (int i = 0; i < templateMessage.getPlaceholders().length; i++) {
				messageData.put(templateMessage.getPlaceholders()[i], new TemplateMessageData(params[i]));
			}

			MpEnvelope envelope = new MpEnvelope(mpDevCredential, weixinOpenId);
			mpPostService.postTemplateMessage(
					envelope,
					templateMessage.getTemplateId(),
					StringUtils.isBlank(url) ? null : WeixinOAuthInterceptor.appendWeixinTicket(url,
							mpPostService.genWeixinTicket(envelope)), messageData);
		}
	}
}
