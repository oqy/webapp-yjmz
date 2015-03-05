package com.minyisoft.webapp.yjmz.common.model;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Optional;
import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.yjmz.common.model.entity.MessageReceiveEntity;

/**
 * @author qingyong_ou 系统私信
 */
@Getter
@Setter
@ModelKey(0x14945B39FCCL)
public class MessageInfo extends CompanyBillBaseInfo {
	@Label("标题")
	@NotBlank
	private String messageTitle;
	@Label("消息接收人集合")
	@NotNull
	@Size(min = 1)
	private List<MessageReceiveEntity> receiveTargets;

	/**
	 * 指定接收人是否已读消息
	 * 
	 * @param receiver
	 * @return
	 */
	public Optional<Boolean> isRead(UserInfo receiver) {
		if (receiver != null && !CollectionUtils.isEmpty(receiveTargets)) {
			for (MessageReceiveEntity entity : receiveTargets) {
				if (receiver.equals(entity.getReceiver())) {
					return entity.getReadDate() != null ? Optional.of(true) : Optional.of(false);
				}
			}
		}
		return Optional.absent();
	}
}
