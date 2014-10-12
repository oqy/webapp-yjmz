package com.minyisoft.webapp.yjmz.common.util.qiniu;

import java.io.IOException;
import java.util.Properties;

/**
 * @author qingyong_ou 七牛配置信息
 */
public final class QiniuConstant {
	private QiniuConstant() {

	}

	private static final Properties properties = new Properties();

	static {
		try {
			properties.load(QiniuConstant.class.getResourceAsStream("qiniuConfig.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static final String UPLOAD_URL = properties.getProperty("upload_url");

	public static final String AK = properties.getProperty("ak");

	public static final String SK = properties.getProperty("sk");

	public static final String BUCKET = properties.getProperty("bucket");

	public static final String BUCKET_URL_PREFIX = properties.getProperty("bucket_url_prefix");

	public static final int TOKEN_EXPIRATION_SECONDS = Integer.parseInt(properties
			.getProperty("tokenExpirationSeconds"));

	public static final int FSIZE_LIMIT_IN_MEGABYTE = Integer.parseInt(properties.getProperty("fsizeLimitInMegaByte"));

	public static final String MIME_LIMIT = properties.getProperty("mimeLimit");
}
