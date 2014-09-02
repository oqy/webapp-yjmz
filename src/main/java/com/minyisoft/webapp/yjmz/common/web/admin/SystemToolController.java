package com.minyisoft.webapp.yjmz.common.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.minyisoft.webapp.core.utils.spring.cache.ModelCacheManager;
import com.minyisoft.webapp.core.web.BaseController;

/**
 * @author qingyong_ou 系统角色管理controller
 */
@Controller
@RequestMapping("/admin")
public class SystemToolController extends BaseController {
	@Autowired
	private ModelCacheManager cacheManager;

	/**
	 * 获取缓存清理页面
	 */
	@RequestMapping(value = "cacheClear.html", method = RequestMethod.GET)
	public String getCacheClearForm() {
		return "admin/cacheClear";
	}

	/**
	 * 清理缓存
	 */
	@RequestMapping(value = "cacheClear.html", method = RequestMethod.POST)
	public String processCacheClearForm(RedirectAttributes redirectAttributes) {
		cacheManager.clearAllCache();
		redirectAttributes.addFlashAttribute("msg", "系统缓存清理完毕");
		return "redirect:cacheClear.html";
	}
}
