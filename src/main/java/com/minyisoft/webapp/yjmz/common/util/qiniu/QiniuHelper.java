package com.minyisoft.webapp.yjmz.common.util.qiniu;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import com.minyisoft.webapp.core.utils.mapper.json.JsonMapper;

public final class QiniuHelper {
	public QiniuHelper() {

	}

	private static final String HMAC_SHA1 = "HmacSHA1";// 七牛指定的签名算法

	/**
	 * 获取七牛upload token
	 */
	public static String generateQiniuUploadToken(String returnUrl, String returnBody) throws JsonProcessingException,
			NoSuchAlgorithmException, InvalidKeyException {
		Map<String, Object> map = Maps.newLinkedHashMap();
		map.put("scope", QiniuConstant.BUCKET);
		map.put("deadline", DateTime.now().plusSeconds(QiniuConstant.TOKEN_EXPIRATION_SECONDS).getMillis() / 1000);
		if (StringUtils.isNotBlank(returnUrl)) {
			map.put("returnUrl", returnUrl);
		}
		if (StringUtils.isNotBlank(returnBody)) {
			map.put("returnBody", returnBody);
		}
		map.put("fsizeLimit", QiniuConstant.FSIZE_LIMIT_IN_MEGABYTE * 1024 * 1024);
		if (StringUtils.isNotBlank(QiniuConstant.MIME_LIMIT)) {
			map.put("mimeLimit", QiniuConstant.MIME_LIMIT);
		}

		byte[] policyBase64 = _encodeBase64Ex(JsonMapper.NON_DEFAULT_MAPPER.getMapper().writeValueAsBytes(map));

		javax.crypto.Mac mac = javax.crypto.Mac.getInstance(HMAC_SHA1);
		SecretKeySpec keySpec = new SecretKeySpec(QiniuConstant.SK.getBytes(), HMAC_SHA1);
		mac.init(keySpec);

		byte[] digest = mac.doFinal(policyBase64);
		byte[] digestBase64 = _encodeBase64Ex(digest);
		byte[] token = new byte[QiniuConstant.AK.getBytes().length + 30 + policyBase64.length];

		System.arraycopy(QiniuConstant.AK.getBytes(), 0, token, 0, QiniuConstant.AK.getBytes().length);
		token[QiniuConstant.AK.getBytes().length] = ':';
		System.arraycopy(digestBase64, 0, token, QiniuConstant.AK.getBytes().length + 1, digestBase64.length);
		token[QiniuConstant.AK.getBytes().length + 29] = ':';
		System.arraycopy(policyBase64, 0, token, QiniuConstant.AK.getBytes().length + 30, policyBase64.length);

		return new String(token);
	}

	/**
	 * 直接使用Base64.encodeBase64URLSafe方法得到的加密串会自动剪除最后的=号，此处复制七牛sdk包的写法
	 * 
	 * @param src
	 * @return
	 */
	private static byte[] _encodeBase64Ex(byte[] src) {
		// urlsafe version is not supported in version 1.4 or lower.
		byte[] b64 = Base64.encodeBase64(src);

		for (int i = 0; i < b64.length; i++) {
			if (b64[i] == '/') {
				b64[i] = '_';
			} else if (b64[i] == '+') {
				b64[i] = '-';
			}
		}
		return b64;
	}
}
