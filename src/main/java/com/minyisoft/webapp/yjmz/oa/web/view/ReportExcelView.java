package com.minyisoft.webapp.yjmz.oa.web.view;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.minyisoft.webapp.core.utils.StringUtils;
import com.minyisoft.webapp.yjmz.common.util.QrCodeGenerator;
import com.minyisoft.webapp.yjmz.oa.model.ReportInfo;

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
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook book, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ReportInfo[] reports = (ReportInfo[]) model.get("reports");
		if (ArrayUtils.isEmpty(reports)) {
			return;
		}

		HSSFPatriarch patriarch;// 画图的顶级管理器，一个sheet只能获取一个
		HSSFClientAnchor anchor;// anchor主要用于设置图片的属性
		ByteArrayOutputStream byteArrayOut;// 存储二维码

		for (ReportInfo report : reports) {
			HSSFSheet sheet = book.cloneSheet(0);
			setText(getCell(sheet, 0, 0), report.getCompany().getName() + "工作报告");
			setText(getCell(sheet, 0, 7), report.getFileNumber());
			setText(getCell(sheet, 1, 7), report.getFileNumber());
			setText(getCell(sheet, 3, 6), DateFormatUtils.format(report.getCreateDate(), "yyyy-MM-dd"));
			setText(getCell(sheet, 4, 1),
					(report.getDepartment() != null ? report.getDepartment().getName() + " " : "")
							+ report.getCreateUser().getName());
			setText(getCell(sheet, 5, 1), report.getReportTitle());
			getCell(sheet, 6, 0).setCellValue(
					new HSSFRichTextString(StringUtils.removeHTMLTag(StringUtils.replace(report.getDescription(),
							"</p>", "\r\n"))));

			// 插入二维码图片
			patriarch = sheet.createDrawingPatriarch();
			anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 8, 4, (short) 8, 5);
			anchor.setAnchorType(3);
			byteArrayOut = new ByteArrayOutputStream();
			QrCodeGenerator.createQrCode(report.getId(), 100, 0, byteArrayOut);
			patriarch
					.createPicture(anchor, book.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
		}
		book.removeSheetAt(0);

		ExcelViewUtils.setExportFileName("工作报告", request, response);
	}

}
