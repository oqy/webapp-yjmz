package com.minyisoft.webapp.yjmz.common.web.manage;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.minyisoft.webapp.core.model.criteria.PageDevice;
import com.minyisoft.webapp.core.web.utils.SelectModuleFilter;
import com.minyisoft.webapp.yjmz.common.model.CompanyInfo;
import com.minyisoft.webapp.yjmz.common.model.DepartmentInfo;
import com.minyisoft.webapp.yjmz.common.model.MessageInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.UserOrgRelationInfo;
import com.minyisoft.webapp.yjmz.common.model.criteria.MessageCriteria;
import com.minyisoft.webapp.yjmz.common.model.criteria.UserOrgRelationCriteria;
import com.minyisoft.webapp.yjmz.common.model.entity.MessageReceiveEntity;
import com.minyisoft.webapp.yjmz.common.model.entity.WeixinTemplateMessage;
import com.minyisoft.webapp.yjmz.common.service.MessageService;
import com.minyisoft.webapp.yjmz.common.service.UserOrgRelationService;

/**
 * @author qingyong_ou 系统私信controller
 */
@Controller
@RequestMapping("/manage")
public class MessageController extends ManageBaseController {
	@Autowired
	private MessageService messageService;
	@Autowired
	private UserOrgRelationService userOrgRelationService;
	// 网站域名
	@Value("#{applicationProperties['web.domain']}")
	private String webDomain;

	/**
	 * 获取收到的私信列表
	 */
	@RequestMapping(value = "messageReceiveList.html", method = RequestMethod.GET)
	public String getMessageReceiveList(@ModelAttribute("currentUser") UserInfo currentUser,
			@ModelAttribute("currentCompany") CompanyInfo currentCompany, MessageCriteria criteria, Model model)
			throws Exception {
		if (criteria.getPageDevice() == null) {
			criteria.setPageDevice(new PageDevice());
		}
		if (criteria.getQueryBeginDate() != null && criteria.getQueryEndDate() != null
				&& criteria.getQueryBeginDate().after(criteria.getQueryEndDate())) {
			criteria.setQueryEndDate(criteria.getQueryBeginDate());
		}
		criteria.setCompany(currentCompany);
		criteria.setReceiver(currentUser);
		criteria.getPageDevice().setTotalRecords(messageService.count(criteria));
		model.addAttribute("receiveMessages", criteria.getPageDevice().getTotalRecords() == 0 ? Collections.emptyList()
				: messageService.getCollection(criteria));

		SelectModuleFilter filter = new SelectModuleFilter(criteria);
		model.addAttribute("filter", filter);
		return "manage/messageReceiveList";
	}

	/**
	 * 异步获取未读私信数量
	 */
	@ResponseBody
	@RequestMapping(value = "countUnreadMessages.do", method = RequestMethod.GET)
	public int countUnreadMessages(@ModelAttribute("currentUser") UserInfo currentUser) {
		MessageCriteria criteria = new MessageCriteria();
		criteria.setReceiver(currentUser);
		criteria.setReceiverRead(false);
		return messageService.count(criteria);
	}

	/**
	 * 异步标记私信已读
	 */
	@ResponseBody
	@RequestMapping(value = "markMessageRead.do", method = RequestMethod.GET)
	public void markMessageRead(@RequestParam("messageId") MessageInfo message,
			@ModelAttribute("currentUser") UserInfo currentUser) {
		messageService.markRead(message, currentUser);
	}

	/**
	 * 获取发送的私信列表
	 */
	@RequestMapping(value = "messageSendList.html", method = RequestMethod.GET)
	public String getMessageSendList(@ModelAttribute("currentUser") UserInfo currentUser,
			@ModelAttribute("currentCompany") CompanyInfo currentCompany, MessageCriteria criteria, Model model)
			throws Exception {
		if (criteria.getPageDevice() == null) {
			criteria.setPageDevice(new PageDevice());
		}
		if (criteria.getQueryBeginDate() != null && criteria.getQueryEndDate() != null
				&& criteria.getQueryBeginDate().after(criteria.getQueryEndDate())) {
			criteria.setQueryEndDate(criteria.getQueryBeginDate());
		}
		criteria.setCompany(currentCompany);
		criteria.setSender(currentUser);
		criteria.getPageDevice().setTotalRecords(messageService.count(criteria));
		model.addAttribute("sendMessages", criteria.getPageDevice().getTotalRecords() == 0 ? Collections.emptyList()
				: messageService.getCollection(criteria));

		SelectModuleFilter filter = new SelectModuleFilter(criteria);
		model.addAttribute("filter", filter);
		return "manage/messageSendList";
	}

	/**
	 * 获取私信发送界面
	 */
	@RequestMapping(value = "messageEdit.html", method = RequestMethod.GET)
	public String getMessageEditForm() {
		return "manage/messageEdit";
	}

	/**
	 * 保存编辑信息
	 */
	@RequestMapping(value = "messageEdit.html", method = RequestMethod.POST)
	public String processMessageEditForm(@ModelAttribute("currentUser") UserInfo currentUser,
			@ModelAttribute("currentCompany") CompanyInfo currentCompany, MessageInfo message,
			@RequestParam(value = "targetDepartments", required = false) DepartmentInfo[] targetDepartments,
			@RequestParam(value = "targetUsers", required = false) UserInfo[] targetUsers) {
		Optional<UserOrgRelationInfo> optionalOrgRelation = currentUser.getOrgRelation(currentCompany);
		if (!optionalOrgRelation.isPresent()) {
			return "redirect:messageSendList.html";
		}
		message.setCompany(currentCompany);
		message.setDepartment(optionalOrgRelation.get().getDepartment());

		List<MessageReceiveEntity> receiveTargets = Lists.newArrayList();
		if (ArrayUtils.isNotEmpty(targetDepartments)) {
			UserOrgRelationCriteria userOrgRelationCriteria = new UserOrgRelationCriteria();
			userOrgRelationCriteria.setDepartments(targetDepartments);
			for (UserOrgRelationInfo relation : userOrgRelationService.getCollection(userOrgRelationCriteria)) {
				if (!currentUser.equals(relation.getUser())) {
					receiveTargets.add(MessageReceiveEntity.createInstance(relation.getUser()));
				}
			}
		} else if (ArrayUtils.isNotEmpty(targetUsers)) {
			for (UserInfo target : targetUsers) {
				if (!currentUser.equals(target)) {
					receiveTargets.add(MessageReceiveEntity.createInstance(target));
				}
			}
		} else {
			for (UserOrgRelationInfo relation : currentCompany.getUserOrgRelations()) {
				if (!currentUser.equals(relation.getUser())) {
					receiveTargets.add(MessageReceiveEntity.createInstance(relation.getUser()));
				}
			}
		}
		message.setReceiveTargets(receiveTargets);
		messageService.submit(message);

		for (MessageReceiveEntity entity : message.getReceiveTargets()) {
			if (StringUtils.isNotBlank(entity.getReceiver().getWeixinOpenId())) {
				messageService.sendWeixinTemplateMessage(entity.getReceiver().getWeixinOpenId(),
						WeixinTemplateMessage.PENDING_TRANSACTION_NOTIFY, webDomain + "/viewDetail.html?billId="
								+ message.getId(), "您有一条新的系统私信\n", message.getMessageTitle(), "系统私信", message
								.getCreateUser().getName(), DateFormatUtils.format(message.getCreateDate(),
								"yyyy年M月d日HH时mm分"), "\n消息内容：" + StringUtils.abbreviate(message.getDescription(), 50)
								+ "\n\n请点击查看详细内容");
			}
		}
		return "redirect:messageSendList.html";
	}
}
