package com.minyisoft.webapp.yjmz.common.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.minyisoft.webapp.core.web.BaseController;

/**
 * @author qingyong_ou 400/404/500错误controller
 */
@Controller
public class ErrorController extends BaseController {
	/**
	 * 400/404/500 error
	 */
	@RequestMapping(value = "/error.html", method = RequestMethod.GET)
	public String getErrorPage() {
		return "error";
	}
}
