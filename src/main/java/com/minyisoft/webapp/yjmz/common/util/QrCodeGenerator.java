package com.minyisoft.webapp.yjmz.common.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * @author qingyong_ou 二维码生成器
 */
public final class QrCodeGenerator {
	private QrCodeGenerator() {

	}

	public static void createQrCode(String content, int qrCodeSize, Integer margin, OutputStream outputStream) {
		try {
			// 根据qrCode的content内容做成ByteMatrix
			Hashtable<EncodeHintType, Object> hintMap = new Hashtable<EncodeHintType, Object>();
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			// 周边空白的宽度，默认10(EncodeHintType.MARGIN不设置时默认为10)
			if (margin != null && margin >= 0) {
				hintMap.put(EncodeHintType.MARGIN, margin);
			}
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix byteMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize, hintMap);

			// 制作qrCode的BufferedImage
			int matrixWidth = byteMatrix.getWidth();
			BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);

			// 用BitMatrix画出并保存qrCode
			for (int i = 0; i < matrixWidth; i++) {
				for (int j = 0; j < matrixWidth; j++) {
					image.setRGB(i, j, byteMatrix.get(i, j) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
				}
			}
			ImageIO.write(image, "JPEG", outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}
}
