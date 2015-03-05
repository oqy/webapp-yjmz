package com.minyisoft.webapp.yjmz.common.model.criteria;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.yjmz.common.model.UserInfo;

@Getter
@Setter
public class MessageCriteria extends CompanyBaseCriteria {
	// 发送人
	private UserInfo sender;
	// 接收人
	private UserInfo receiver;
	// 接收人已读私信
	private Boolean receiverRead;
}
