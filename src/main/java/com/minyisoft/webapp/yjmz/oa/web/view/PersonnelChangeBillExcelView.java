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

import com.minyisoft.webapp.yjmz.common.util.QrCodeGenerator;
import com.minyisoft.webapp.yjmz.oa.model.PersonnelChangeBillInfo;
import com.minyisoft.webapp.yjmz.oa.model.enumField.PersonnelChangeTypeEnum;

/**
 * @author qingyong_ou 人事变动单Excel View
 * 
 */
@Component
public class PersonnelChangeBillExcelView extends AbstractExcelView {

	public PersonnelChangeBillExcelView() {
		super.setUrl("classpath:/com/minyisoft/webapp/yjmz/oa/web/view/personnelChangeBill");
	}

	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook book, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PersonnelChangeBillInfo[] personnelChangeBills = (PersonnelChangeBillInfo[]) model.get("personnelChangeBills");
		if (ArrayUtils.isEmpty(personnelChangeBills)) {
			return;
		}

		HSSFPatriarch patriarch;// 画图的顶级管理器，一个sheet只能获取一个
		HSSFClientAnchor anchor;// anchor主要用于设置图片的属性
		ByteArrayOutputStream byteArrayOut;// 存储二维码

		for (PersonnelChangeBillInfo personnelChangeBill : personnelChangeBills) {
			HSSFSheet sheet = book.cloneSheet(0);
			book.setSheetName(book.getSheetIndex(sheet),
					(personnelChangeBill.getDepartment() != null ? personnelChangeBill.getDepartment().getName() : "")
							+ personnelChangeBill.getStaffName());
			setText(getCell(sheet, 0, 0), personnelChangeBill.getCompany().getName() + "人事变动单");
			setText(getCell(sheet, 1, 1), personnelChangeBill.getStaffName());
			setText(getCell(sheet, 1, 4), personnelChangeBill.getUserMale().getDescription());
			setText(getCell(sheet, 1, 7), DateFormatUtils.format(personnelChangeBill.getEntryDate(), "yyyy-MM-dd"));
			setText(getCell(sheet, 2, 1), personnelChangeBill.getStaffNumber());
			setText(getCell(sheet, 2, 7), DateFormatUtils.format(personnelChangeBill.getEffectiveDate(), "yyyy-MM-dd"));

			setText(getCell(sheet, 4, 0),
					(personnelChangeBill.getChangeType() == PersonnelChangeTypeEnum.PROBATION_FINISH ? "■" : "□")
							+ PersonnelChangeTypeEnum.PROBATION_FINISH.getDescription());
			setText(getCell(sheet, 4, 2),
					(personnelChangeBill.getChangeType() == PersonnelChangeTypeEnum.PROMOTION ? "■" : "□")
							+ PersonnelChangeTypeEnum.PROMOTION.getDescription());
			setText(getCell(sheet, 4, 5),
					(personnelChangeBill.getChangeType() == PersonnelChangeTypeEnum.INTERNAL_TRANSFER ? "■" : "□")
							+ PersonnelChangeTypeEnum.INTERNAL_TRANSFER.getDescription());
			setText(getCell(sheet, 4, 7),
					(personnelChangeBill.getChangeType() == PersonnelChangeTypeEnum.SALARY_ADJUSTMENT ? "■" : "□")
							+ PersonnelChangeTypeEnum.SALARY_ADJUSTMENT.getDescription());
			setText(getCell(sheet, 5, 0), (personnelChangeBill.getChangeType() == PersonnelChangeTypeEnum.OTHER ? "■"
					: "□") + PersonnelChangeTypeEnum.OTHER.getDescription());
			setText(getCell(sheet, 5, 1),
					personnelChangeBill.getChangeType() == PersonnelChangeTypeEnum.OTHER ? personnelChangeBill
							.getOtherChangeType() : "");

			setText(getCell(sheet, 8, 2), personnelChangeBill.getDepartment() != null ? personnelChangeBill
					.getDepartment().getName() : "");
			setText(getCell(sheet, 8, 6), personnelChangeBill.getNewDepartment() != null ? personnelChangeBill
					.getNewDepartment().getName() : "");
			setText(getCell(sheet, 9, 2),
					personnelChangeBill.getOriPosition() != null ? personnelChangeBill.getOriPosition() : "");
			setText(getCell(sheet, 9, 6),
					personnelChangeBill.getNewPosition() != null ? personnelChangeBill.getNewPosition() : "");
			setText(getCell(sheet, 10, 2), personnelChangeBill.getOriRank() != null ? personnelChangeBill.getOriRank()
					: "");
			setText(getCell(sheet, 10, 6), personnelChangeBill.getNewRank() != null ? personnelChangeBill.getNewRank()
					: "");
			setText(getCell(sheet, 11, 3), personnelChangeBill.getOriSalary() != null ? personnelChangeBill
					.getOriSalary().toString() : "");
			setText(getCell(sheet, 11, 7), personnelChangeBill.getNewSalary() != null ? personnelChangeBill
					.getNewSalary().toString() : "");

			setText(getCell(sheet, 13, 2),
					personnelChangeBill.getLastWorkDate() != null ? DateFormatUtils.format(
							personnelChangeBill.getLastWorkDate(), "yyyy-MM-dd") : "");
			if (personnelChangeBill.getSalaryChangeAmount().compareTo(BigDecimal.ZERO) >= 0) {
				setText(getCell(sheet, 14, 2), personnelChangeBill.getNewSalary() != null ? personnelChangeBill
						.getSalaryChangeAmount().toString() : "");
				setText(getCell(sheet, 14, 5),
						personnelChangeBill.getSalaryChangeReason() != null ? personnelChangeBill
								.getSalaryChangeReason() : "");
			} else {
				setText(getCell(sheet, 15, 2), personnelChangeBill.getNewSalary() != null ? personnelChangeBill
						.getSalaryChangeAmount().abs().toString() : "");
				setText(getCell(sheet, 15, 5),
						personnelChangeBill.getSalaryChangeReason() != null ? personnelChangeBill
								.getSalaryChangeReason() : "");
			}
			setText(getCell(sheet, 16, 1),
					personnelChangeBill.getDescription() != null ? personnelChangeBill.getDescription() : "");
			setText(getCell(sheet, 17, 1),
					personnelChangeBill.getHrRemark() != null ? personnelChangeBill.getHrRemark() : "");

			// 插入二维码图片
			patriarch = sheet.createDrawingPatriarch();
			anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 9, 0, (short) 9, 0);
			anchor.setAnchorType(3);
			byteArrayOut = new ByteArrayOutputStream();
			QrCodeGenerator.createQrCode("view:" + personnelChangeBill.getId(), 100, 0, byteArrayOut);
			patriarch
					.createPicture(anchor, book.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
		}
		book.removeSheetAt(0);

		ExcelViewUtils.setExportFileName("人事变动单", request, response);
	}

}
