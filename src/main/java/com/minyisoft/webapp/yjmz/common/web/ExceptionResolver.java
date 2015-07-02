package com.minyisoft.webapp.yjmz.common.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public class ExceptionResolver implements HandlerExceptionResolver {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/*
	 * 统一处理页面异常，通常返回具体的出错提示页面
	 */
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		logger.error(ex.getMessage(), ex);
		ModelAndView modelAndView = new ModelAndView("error");
		modelAndView.addObject("error", ex.getMessage());
		return modelAndView;
	}
}
