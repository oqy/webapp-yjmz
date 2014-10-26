package com.minyisoft.webapp.yjmz.common.persistence;

import org.apache.ibatis.annotations.Param;

import com.minyisoft.webapp.core.persistence.BaseDao;
import com.minyisoft.webapp.yjmz.common.model.MessageInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.MessageCriteria;

public interface MessageDao extends BaseDao<MessageInfo, MessageCriteria> {
	/**
	 * 添加消息接收人
	 */
	int addMessageReceiver(@Param("message") MessageInfo message, @Param("receiver") UserInfo receiver);

	/**
	 * 更新消息接收人消息阅读时间
	 */
	int updateMessageReadDate(@Param("message") MessageInfo message, @Param("receiver") UserInfo receiver);
}
