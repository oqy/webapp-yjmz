package com.minyisoft.webapp.yjmz.weixin.web;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
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

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.security.utils.PermissionUtils;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.core.web.BaseController;
import com.minyisoft.webapp.weixin.common.model.dto.receive.EventMessage;
import com.minyisoft.webapp.weixin.common.model.dto.receive.EventType;
import com.minyisoft.webapp.weixin.common.model.dto.receive.MenuMessage;
import com.minyisoft.webapp.weixin.common.model.dto.receive.Message;
import com.minyisoft.webapp.weixin.common.model.dto.receive.MessageConverter;
import com.minyisoft.webapp.weixin.common.model.dto.receive.MessageType;
import com.minyisoft.webapp.weixin.common.model.dto.receive.ScanCodeMenuMessage;
import com.minyisoft.webapp.weixin.common.model.dto.receive.TransferCustomerService;
import com.minyisoft.webapp.weixin.common.model.dto.receive.messagenode.ScanCodeConverter;
import com.minyisoft.webapp.weixin.common.model.dto.send.Article;
import com.minyisoft.webapp.weixin.common.service.WeixinCommonService;
import com.minyisoft.webapp.weixin.common.service.WeixinPostService;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;
import com.minyisoft.webapp.yjmz.common.model.entity.WeixinTemplateMessage;
import com.minyisoft.webapp.yjmz.common.service.MessageService;
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
	private WeixinCommonService weixinCommonService;
	@Autowired
	private WeixinPostService weixinPostService;
	@Autowired
	private MessageService messageService;

	// 公众号微信号
	@Value("#{applicationProperties['weixin.weixinNumber']}")
	private String weixinNumber;
	// 是否启用微信多客服
	@Value("#{applicationProperties['weixin.dkf_enabled']}")
	private boolean dkfEnabled;
	// 关注微信公众号后的欢迎语
	@Value("#{applicationProperties['weixin.welcome_subscribe']}")
	private String welcomeSubscribe;
	// 网站域名
	@Value("#{applicationProperties['web.domain']}")
	private String webDomain;
	// 栏目建设中图片
	@Value("#{applicationProperties['weixin.build_column_pic']}")
	private String buildColumnPicPath;
	// 在线办公栏目图片
	@Value("#{applicationProperties['weixin.web_oa_pic']}")
	private String webOAPicPath;

	public WeixinController() {
		xStream = new XStream();
		xStream.alias("xml", Message.class);
		xStream.autodetectAnnotations(true);
		xStream.registerConverter(new MessageConverter());
		xStream.registerConverter(new ScanCodeConverter());
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
			weixinPostService.postTextMessage(message.getFromUserName(), welcomeSubscribe);
		}
		// 取消关注
		else if (message instanceof EventMessage && ((EventMessage) message).getEvent() == EventType.UNSUBSCRIBE) {
			UserInfo user = userService.getValue(message.getFromUserName());
			// 清除用户openId信息
			if (user != null) {
				PermissionUtils.stopPermissionCheck();
				user.setWeixinOpenId(null);
				userService.save(user);
				PermissionUtils.startPermissionCheck();
			}
		}
		// 点击菜单
		else if (message instanceof MenuMessage) {
			WeixinMenu menu = WeixinMenu.valueOf(StringUtils.upperCase(((MenuMessage) message).getEventKey()));
			switch (menu) {
			case WEBOA:
				Article article = new Article();
				article.setTitle("雍憬明珠微办公系统");
				article.setDescription("欢迎使用雍憬明珠微办公系统，请点击登录。");
				article.setURL(WeixinOAuthInterceptor.appendWeixinTicket(webDomain + "/login.html",
						weixinCommonService.genWeixinTicket(message.getFromUserName())));
				article.setPicurl(webOAPicPath);
				weixinPostService.postNewsMessage(message.getFromUserName(), article);
				break;
			case SCAN_CODE:
				if (message instanceof ScanCodeMenuMessage
						&& ((ScanCodeMenuMessage) message).getScanCodeInfo().getScanType().equalsIgnoreCase("qrcode")) {
					String scanResult = StringUtils.removeStartIgnoreCase(((ScanCodeMenuMessage) message)
							.getScanCodeInfo().getScanResult(), "view:");
					IModelObject model = ObjectUuidUtils.getObject(scanResult);
					if (model instanceof WorkFlowBusinessModel
							&& ((WorkFlowBusinessModel) model).getProcessStatus() != null) {
						_postWorkFlowBusinessModelDetail((WorkFlowBusinessModel) model, message.getFromUserName());
					} else {
						weixinPostService.postTextMessage(message.getFromUserName(), "抱歉，不存在指定的工作流任务信息");
					}
				}
				break;
			default:
				article = new Article();
				article.setTitle("栏目建设中");
				article.setDescription("栏目建设中，敬请期待。");
				article.setPicurl(buildColumnPicPath);
				weixinPostService.postNewsMessage(message.getFromUserName(), article);
				break;
			}
		}
	}

	/**
	 * 微信菜单
	 * 
	 * @author qingyong_ou
	 * 
	 */
	private enum WeixinMenu {
		HOTEL, REPAST, WEBOA, SCAN_CODE
	}

	/**
	 * 发送工作流任务信息
	 */
	private void _postWorkFlowBusinessModelDetail(WorkFlowBusinessModel model, String weixinOpenId) {
		messageService.sendWeixinTemplateMessage(weixinOpenId, WeixinTemplateMessage.ORDER_STATUS_NOTIFY, webDomain
				+ "/viewDetail.html?billId=" + model.getId(), "工作流任务概况\n", ((WorkFlowBusinessModel) model)
				.getProcessInstanceName(), ((WorkFlowBusinessModel) model).getProcessStatus().getDescription(),
				DateFormatUtils.format(((WorkFlowBusinessModel) model).getCreateDate(), "yyyy年M月d日HH时mm分"),
				"\n点击查看工作流任务详情");
	}
}
