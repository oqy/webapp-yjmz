package com.minyisoft.webapp.yjmz.oa.web.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;

/**
 * @author qingyong_ou 工作报告Excel View
 * 
 */
@Component
public class ReportExcelView extends AbstractExcelView {

	public ReportExcelView() {
		super.setUrl("classpath:/com/minyisoft/webapp/yjmz/oa/web/view/report");
	}

	@Override
	protected void buildExcelDocument(Map<String, Object> arg0, HSSFWorkbook arg1, HttpServletRequest arg2,
			HttpServletResponse arg3) throws Exception {
	}

}
