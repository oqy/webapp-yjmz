package com.minyisoft.webapp.yjmz.weixin.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Optional;
import com.minyisoft.webapp.core.exception.ServiceException;
import com.minyisoft.webapp.core.utils.mapper.json.JsonMapper;
import com.minyisoft.webapp.yjmz.weixin.dto.send.Article;
import com.minyisoft.webapp.yjmz.weixin.dto.send.NewsMessage;
import com.minyisoft.webapp.yjmz.weixin.dto.send.TextMessage;

@Service
public class WeixinConnectService {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${weixin.appID}")
	private String weixinAppID;
	@Value("${weixin.appsecret}")
	private String weixinAppSecret;
	@Value("${weixin.access_token_url}")
	private String accessTokenUrl;
	@Value("${weixin.oauth2.access_token_url}")
	private String oauth2AccessTokenUrl;
	@Value("${weixin.send_message_url}")
	private String sendMessageUrl;
	@Autowired
	private RestTemplate restTemplate;

	// 微信access_token在redis的键值
	//private static final String WEIXIN_ACCESS_TOKEN_KEY = "weixin:access_token";

	/**
	 * 获取微信access_token
	 * 
	 * @return
	 */
	public String getAccessToken() {
		//String accessToken = null;
		/*if (StringUtils.isNotBlank(accessToken = jedisTemplate.get(WEIXIN_ACCESS_TOKEN_KEY))) {
			return accessToken;
		}*/
		Map<String, String> resultMap = _queryFromWeixinServer(MessageFormat.format(accessTokenUrl, weixinAppID,
				weixinAppSecret));
		if (resultMap.containsKey("access_token")) {
			// 在redis中缓存access_token，过期时间比微信官方时间短1分钟
			//jedisTemplate.setex(WEIXIN_ACCESS_TOKEN_KEY, resultMap.get("access_token"),
					//Integer.parseInt(resultMap.get("expires_in")) - 60);
			return resultMap.get("access_token");
		} else {
			logger.error(MessageFormat.format("获取微信access_token失败，错误码：{0}，错误提示：{1}", resultMap.get("errcode"),
					resultMap.get("errmsg")));
		}
		throw new ServiceException("无法获取微信access_token");
	}

	/**
	 * 在网页授权的情况下，通过code获取微信用户对应本服务号的openId
	 * 
	 * @param weixinOAuthCode
	 * @return
	 */
	public Optional<String> getOpenId(String weixinOAuthCode) {
		if (StringUtils.isBlank(weixinOAuthCode)) {
			return Optional.absent();
		}
		Map<String, String> resultMap = _queryFromWeixinServer(MessageFormat.format(oauth2AccessTokenUrl, weixinAppID,
				weixinAppSecret, weixinOAuthCode));
		if (resultMap.containsKey("openid")) {
			return Optional.of(resultMap.get("openid"));
		} else {
			logger.error(MessageFormat.format("通过网页授权code获取微信openId失败，错误码：{0}，错误提示：{1}", resultMap.get("errcode"),
					resultMap.get("errmsg")));
		}
		return Optional.absent();
	}

	/**
	 * 查询微信指定url获取结果
	 * 
	 * @param url
	 * @return
	 */
	private Map<String, String> _queryFromWeixinServer(String url) {
		ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
		return JsonMapper.NON_EMPTY_MAPPER.fromJson(result.getBody(), JsonMapper.NON_EMPTY_MAPPER.getMapper()
				.getTypeFactory().constructMapType(Map.class, String.class, String.class));
	}

	/**
	 * 向指定微信用户发送文本消息
	 * 
	 * @param weixinOpenId
	 * @param content
	 * @return
	 */
	public boolean postTextMessage(String weixinOpenId, String content) {
		Assert.hasLength(weixinOpenId, "待发消息目标微信号不能为空");
		Assert.hasLength(weixinOpenId, "待发消息不能为空");

		TextMessage m = new TextMessage();
		m.setToUser(weixinOpenId);
		m.setContent(content);
		Map<String, String> resultMap = _postToWeixinServer(MessageFormat.format(sendMessageUrl, getAccessToken()),
				JsonMapper.NON_EMPTY_MAPPER.toJson(m));
		if (resultMap.containsKey("errcode") && !"0".equals(resultMap.get("errcode"))) {
			logger.error("发送微信文本消息失败，目标openId:" + weixinOpenId + "，错误码：" + resultMap.get("errcode"));
			return false;
		}
		return true;
	}

	/**
	 * 发送图文消息
	 * 
	 * @param weixinOpenId
	 * @param articles
	 * @return
	 */
	public boolean postNewsMessage(String weixinOpenId, List<Article> articles) {
		Assert.hasLength(weixinOpenId, "待发消息目标微信号不能为空");
		Assert.isTrue(articles != null && !articles.isEmpty(), "待发消息不能为空");

		NewsMessage m = new NewsMessage();
		m.setArticles(articles);
		m.setToUser(weixinOpenId);
		Map<String, String> resultMap = _postToWeixinServer(MessageFormat.format(sendMessageUrl, getAccessToken()),
				JsonMapper.NON_EMPTY_MAPPER.toJson(m));
		if (resultMap.containsKey("errcode") && !"0".equals(resultMap.get("errcode"))) {
			logger.error("发送微信文本消息失败，目标openId:" + weixinOpenId + "，错误码：" + resultMap.get("errcode"));
			return false;
		}
		return true;
	}

	/**
	 * 向微信服务器指定url发送post请求
	 * 
	 * @param url
	 * @param request
	 * @return
	 */
	private Map<String, String> _postToWeixinServer(String url, Object request) {
		ResponseEntity<String> result = restTemplate.postForEntity(url, request, String.class);
		return JsonMapper.NON_EMPTY_MAPPER.fromJson(result.getBody(), JsonMapper.NON_EMPTY_MAPPER.getMapper()
				.getTypeFactory().constructMapType(Map.class, String.class, String.class));
	}
}
