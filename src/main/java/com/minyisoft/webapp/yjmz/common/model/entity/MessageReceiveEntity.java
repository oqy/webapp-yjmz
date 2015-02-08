package com.minyisoft.webapp.yjmz.common.model.entity;

import java.util.Date;

import org.springframework.util.Assert;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.yjmz.common.model.UserInfo;

/**
 * @author qingyong_ou 消息接收对象
 */
@Getter
@Setter
public class MessageReceiveEntity {
	// 接收人
	private UserInfo receiver;
	// 阅读时间
	private Date readDate;

	public static final MessageReceiveEntity createInstance(UserInfo receiver) {
		Assert.notNull(receiver, "未指定私信接收人");
		MessageReceiveEntity entity = new MessageReceiveEntity();
		entity.setReceiver(receiver);
		return entity;
	}
}
