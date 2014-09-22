package com.minyisoft.webapp.yjmz.weixin.web;

import java.text.MessageFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.minyisoft.webapp.core.web.BaseController;
import com.minyisoft.webapp.weixin.common.model.dto.receive.EventMessage;
import com.minyisoft.webapp.weixin.common.model.dto.receive.EventType;
import com.minyisoft.webapp.weixin.common.model.dto.receive.Message;
import com.minyisoft.webapp.weixin.common.model.dto.receive.MessageConverter;
import com.minyisoft.webapp.weixin.common.model.dto.receive.MessageType;
import com.minyisoft.webapp.weixin.common.model.dto.receive.TransferCustomerService;
import com.minyisoft.webapp.weixin.common.service.WeixinPostService;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.service.UserService;
import com.minyisoft.webapp.yjmz.weixin.web.interceptor.WeixinOAuthInterceptor;
import com.thoughtworks.xstream.XStream;

/**
 * @author yongan_cui 微信公众平台controller
 */
@Controller
@RequestMapping("/weixin")
public class WeixinController extends BaseController {
	private XStream xStream;
	@Autowired
	private TaskExecutor taskExecutor;
	@Autowired
	private UserService userService;
	@Autowired
	private WeixinPostService weixinPostService;

	// 公众号微信号
	@Value("#{applicationProperties['weixin.weixinNumber']}")
	private String weixinNumber;
	// 公众号微信号
	@Value("#{applicationProperties['weixin.dkf_enabled']}")
	private boolean dkfEnabled;
	// 关注微信公众号后的欢迎语
	@Value("#{applicationProperties['weixin.welcome_subscribe']}")
	private String welcomeSubscribe;
	// 网站域名
	@Value("#{applicationProperties['web.domain']}")
	private String webDomain;

	public WeixinController() {
		xStream = new XStream();
		xStream.alias("xml", Message.class);
		xStream.autodetectAnnotations(true);
		xStream.registerConverter(new MessageConverter());
	}

	/**
	 * 申请接入
	 */
	@RequestMapping(value = "receiveMessage.html", method = RequestMethod.GET, params = "echostr")
	public ResponseEntity<String> apply(@RequestParam String echostr) {
		logger.info("验证接入微信，原样返回echostr参数内容");
		return new ResponseEntity<String>(echostr, HttpStatus.OK);
	}

	/**
	 * 接收信息，采用新线程处理信息，避免形成阻塞
	 */
	@RequestMapping(value = "receiveMessage.html", method = RequestMethod.POST)
	public ResponseEntity<String> receiveMessage(@RequestBody final String messageString) {
		logger.info("接收来自微信的信息：" + messageString);
		final Message message = (Message) xStream.fromXML(messageString);
		// 只处理系统定义了的消息
		if (message != null) {
			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						_processReceivedMessage(message, messageString);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			});

			// 非事件推送消息，将消息转发到多客服
			if (dkfEnabled && !(message instanceof EventMessage)) {
				TransferCustomerService tcs = new TransferCustomerService();
				tcs.setFromUserName(weixinNumber);
				tcs.setToUserName(message.getFromUserName());
				tcs.setCreateTime(message.getCreateTime());
				tcs.setMsgType(MessageType.TRANSFER_CUSTOMER_SERVICE);
				logger.info("转发多客服消息：" + xStream.toXML(tcs));
				return new ResponseEntity<String>(xStream.toXML(tcs), HttpStatus.OK);
			}
		}
		return new ResponseEntity<String>("", HttpStatus.OK);
	}

	/**
	 * 处理微信消息
	 */
	private void _processReceivedMessage(final Message message, final String messageString) {
		// 关注
		if (message instanceof EventMessage && ((EventMessage) message).getEvent() == EventType.SUBSCRIBE) {
			weixinPostService.postTextMessage(
					message.getFromUserName(),
					MessageFormat.format(
							welcomeSubscribe,
							new StringBuffer(webDomain).append("/login.html?")
									.append(WeixinOAuthInterceptor.WEIXIN_OPEN_ID).append("&")
									.append(message.getFromUserName())).toString());
		}
		// 取消关注
		else if (message instanceof EventMessage && ((EventMessage) message).getEvent() == EventType.UNSUBSCRIBE) {
			UserInfo user = userService.getValue(message.getFromUserName());
			// 清除用户openId信息
			if (user != null) {
				user.setWeixinOpenId(null);
				userService.save(user);
			}
		}
	}
}