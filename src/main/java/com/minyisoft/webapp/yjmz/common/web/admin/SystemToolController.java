package com.minyisoft.webapp.yjmz.common.web.admin;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Maps;
import com.minyisoft.webapp.core.utils.spring.cache.ModelCacheManager;
import com.minyisoft.webapp.core.web.BaseController;
import com.minyisoft.webapp.weixin.common.service.WeixinCommonService;

/**
 * @author qingyong_ou 系统角色管理controller
 */
@Controller
@RequestMapping("/admin")
public class SystemToolController extends BaseController {
	@Autowired
	private ModelCacheManager cacheManager;
	@Autowired
	private WeixinCommonService weixinCommonService;

	/**
	 * 获取缓存清理页面
	 */
	@RequestMapping(value = "cacheManager.html", method = RequestMethod.GET)
	public String getCacheManager() {
		return "admin/cacheManager";
	}

	/**
	 * 异步获取微信access token
	 */
	@RequestMapping(value = "getWeixinAccessToken.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> getWeixinAccessToken() {
		Map<String, String> accessTokenMap = Maps.newHashMap();
		accessTokenMap.put("access_token", weixinCommonService.getAccessToken());
		return accessTokenMap;
	}

	/**
	 * 清理对象缓存
	 */
	@RequestMapping(value = "cacheClear.html", method = RequestMethod.POST, params = "clearModelCache")
	public String clearModelCache(RedirectAttributes redirectAttributes) {
		cacheManager.clearAllCache();
		redirectAttributes.addFlashAttribute("msg", "系统缓存清理完毕");
		return "redirect:cacheManager.html";
	}

	/**
	 * 清理微信access token缓存
	 */
	@RequestMapping(value = "cacheClear.html", method = RequestMethod.POST, params = "clearWeixinAccessToken")
	public String clearWeixinAccessTokenCache(RedirectAttributes redirectAttributes) {
		weixinCommonService.clearAccessTokenCache();
		redirectAttributes.addFlashAttribute("msg", "微信access token缓存清理完毕");
		return "redirect:cacheManager.html";
	}
}
