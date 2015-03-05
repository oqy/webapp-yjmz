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

import com.google.common.base.Optional;
import com.minyisoft.webapp.yjmz.common.util.QrCodeGenerator;
import com.minyisoft.webapp.yjmz.oa.model.MaintainReqBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.enumField.MaintainTypeEnum;

/**
 * @author qingyong_ou 工作报告Excel View
 * 
 */
@Component
public class MaintainReqBillExcelView extends AbstractExcelView {

	public MaintainReqBillExcelView() {
		super.setUrl("classpath:/com/minyisoft/webapp/yjmz/oa/web/view/maintainReqBill");
	}

	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook book, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		MaintainReqBillInfo[] maintainReqBills = (MaintainReqBillInfo[]) model.get("maintainReqBills");
		if (ArrayUtils.isEmpty(maintainReqBills)) {
			return;
		}

		HSSFPatriarch patriarch;// 画图的顶级管理器，一个sheet只能获取一个
		HSSFClientAnchor anchor;// anchor主要用于设置图片的属性
		ByteArrayOutputStream byteArrayOut;// 存储二维码
		for (MaintainReqBillInfo maintainReqBill : maintainReqBills) {
			HSSFSheet sheet = book.cloneSheet(0);
			setText(getCell(sheet, 0, 0), maintainReqBill.getCompany().getName() + "工程维修通知单");
			setText(getCell(sheet, 1, 1), maintainReqBill.getDepartment() != null ? maintainReqBill.getDepartment()
					.getName() + " " : "");
			setText(getCell(sheet, 1, 5), DateFormatUtils.format(maintainReqBill.getCreateDate(), "yyyy-MM-dd"));
			setText(getCell(sheet, 2, 1), maintainReqBill.getCreateUser().getName());
			setText(getCell(sheet, 2, 5), maintainReqBill.getLocation());

			int maintainTypeCellIndex = 0;
			for (MaintainTypeEnum type : MaintainTypeEnum.values()) {
				setText(getCell(sheet, 3, maintainTypeCellIndex),
						(ArrayUtils.contains(maintainReqBill.getMaintainTypes(), type) ? "■" : "□")
								+ type.getDescription());
				maintainTypeCellIndex += 2;
			}
			setText(getCell(sheet, 4, 0), maintainReqBill.getDescription());

			setText(getCell(sheet, 5, 1), maintainReqBill.getMaintenanceMan() != null ? maintainReqBill
					.getMaintenanceMan().getName() : "");
			setText(getCell(sheet, 5, 5),
					maintainReqBill.getFinishDate() != null ? DateFormatUtils.format(maintainReqBill.getFinishDate(),
							"yyyy-MM-dd") : "");
			for (int i = 0; i < maintainReqBill.getEntry().size(); i++) {
				setText(getCell(sheet, 7 + i, 0), maintainReqBill.getEntry().get(i).getName());
				setText(getCell(sheet, 7 + i, 4), String.valueOf(maintainReqBill.getEntry().get(i).getQuantity()));
				setText(getCell(sheet, 7 + i, 6), String.valueOf(maintainReqBill.getEntry().get(i).getPrice()));
			}

			Optional<BigDecimal> materialsTotalQuantity = maintainReqBill.getMaterialsTotalQuantity();
			setText(getCell(sheet, 11, 4),
					materialsTotalQuantity.isPresent() ? String.valueOf(materialsTotalQuantity.get()) : "");
			Optional<BigDecimal> materialsTotalPrice = maintainReqBill.getMaterialsTotalPrice();
			setText(getCell(sheet, 11, 6), materialsTotalPrice.isPresent() ? String.valueOf(materialsTotalPrice.get())
					: "");

			setText(getCell(sheet, 12, 1), maintainReqBill.getReceiver() != null ? maintainReqBill.getReceiver()
					.getName() : "");
			setText(getCell(sheet, 12, 5), maintainReqBill.getExaminer() != null ? maintainReqBill.getExaminer()
					.getName() : "");

			// 插入二维码
			patriarch = sheet.createDrawingPatriarch();
			anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 7, 1, (short) 7, 2);
			anchor.setAnchorType(3);
			byteArrayOut = new ByteArrayOutputStream();
			QrCodeGenerator.createQrCode("view:" + maintainReqBill.getId(), 100, 0, byteArrayOut);
			patriarch
					.createPicture(anchor, book.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
		}
		book.removeSheetAt(0);

		ExcelViewUtils.setExportFileName("工程维修通知单", request, response);
	}

}
