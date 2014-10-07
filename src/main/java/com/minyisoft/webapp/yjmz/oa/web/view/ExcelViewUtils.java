package com.minyisoft.webapp.yjmz.oa.web.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

/**
 * @author qingyong_ou excel view帮助类
 */
public final class ExcelViewUtils {
	private static final Logger logger = LoggerFactory.getLogger(ExcelViewUtils.class);

	private ExcelViewUtils() {

	}

	/**
	 * 设置导出excel文件的文件名
	 * 
	 * @param filename
	 * @param request
	 * @param response
	 */
	public static void setExportFileName(String filename, HttpServletRequest request, HttpServletResponse response) {
		try {
			String agent = request.getHeader("USER-AGENT");
			if (null != agent && -1 != agent.indexOf("MSIE") || null != agent && -1 != agent.indexOf("Trident")) {// ie
				filename = java.net.URLEncoder.encode(filename, Charsets.UTF_8.name());
			} else if (null != agent && -1 != agent.indexOf("Mozilla")) {// 火狐,chrome等
				filename = new String(filename.getBytes(Charsets.UTF_8), Charsets.ISO_8859_1);
			}

			response.setHeader("Content-Disposition", "attachment; filename=" + filename + ".xls");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
