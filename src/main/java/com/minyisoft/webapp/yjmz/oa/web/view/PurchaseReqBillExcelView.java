package com.minyisoft.webapp.yjmz.oa.web.view;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.minyisoft.webapp.core.utils.StringUtils;
import com.minyisoft.webapp.yjmz.common.util.QrCodeGenerator;
import com.minyisoft.webapp.yjmz.oa.model.PurchaseReqBillInfo;

/**
 * @author qingyong_ou 工作报告Excel View
 * 
 */
@Component
public class PurchaseReqBillExcelView extends AbstractExcelView {

	public PurchaseReqBillExcelView() {
		super.setUrl("classpath:/com/minyisoft/webapp/yjmz/oa/web/view/purchaseReqBill");
	}

	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook book, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PurchaseReqBillInfo[] purchaseReqBills = (PurchaseReqBillInfo[]) model.get("purchaseReqBills");
		if (ArrayUtils.isEmpty(purchaseReqBills)) {
			return;
		}

		HSSFPatriarch patriarch;// 画图的顶级管理器，一个sheet只能获取一个
		HSSFClientAnchor anchor;// anchor主要用于设置图片的属性
		ByteArrayOutputStream byteArrayOut;// 存储二维码

		for (PurchaseReqBillInfo purchaseReqBill : purchaseReqBills) {
			HSSFSheet sheet = book.cloneSheet(0);
			setText(getCell(sheet, 0, 0), purchaseReqBill.getCompany().getName() + "采购单");
			setText(getCell(sheet, 1, 1), purchaseReqBill.getDepartment() != null ? purchaseReqBill.getDepartment()
					.getName() : "");
			setText(getCell(sheet, 1, 7), DateFormatUtils.format(purchaseReqBill.getCreateDate(), "yyyy-MM-dd"));

			BigDecimal totalPrice = BigDecimal.ZERO; // 合计总额
			for (int i = 0; i < purchaseReqBill.getEntry().size(); i++) {
				setText(getCell(sheet, 4 + i, 0), purchaseReqBill.getEntry().get(i).getName());
				setText(getCell(sheet, 4 + i, 4), purchaseReqBill.getEntry().get(i).getStandard());
				setText(getCell(sheet, 4 + i, 6), String.valueOf(purchaseReqBill.getEntry().get(i).getQuantity()));
				setText(getCell(sheet, 4 + i, 7), String.valueOf(purchaseReqBill.getEntry().get(i).getUnitPrice()));
				setText(getCell(sheet, 4 + i, 8), purchaseReqBill.getEntry().get(i).getRemark());

				totalPrice = totalPrice.add(purchaseReqBill.getEntry().get(i).getUnitPrice()
						.multiply(purchaseReqBill.getEntry().get(i).getQuantity())
						.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			setText(getCell(sheet, 10, 6), StringUtils.convert2RMB(totalPrice));

			// 插入二维码图片
			patriarch = sheet.createDrawingPatriarch();
			anchor = new HSSFClientAnchor(0, 100, 1023, 255, (short) 12, 0, (short) 12, 1);
			anchor.setAnchorType(3);
			byteArrayOut = new ByteArrayOutputStream();
			QrCodeGenerator.createQrCode("view:" + purchaseReqBill.getId(), 100, 0, byteArrayOut);
			patriarch
					.createPicture(anchor, book.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
		}
		book.removeSheetAt(0);

		ExcelViewUtils.setExportFileName("采购单", request, response);
	}
}
