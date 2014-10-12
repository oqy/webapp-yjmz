package com.minyisoft.webapp.yjmz.common.web.manage;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.minyisoft.webapp.core.utils.mapper.json.JsonMapper;
import com.minyisoft.webapp.yjmz.common.model.AttachmentInfo;
import com.minyisoft.webapp.yjmz.common.model.CompanyInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.service.AttachmentService;

/**
 * @author qingyong_ou 附件上传controller
 */
@Controller
@RequestMapping("/manage")
public class AttachmentController extends ManageBaseController {
	@Autowired
	private AttachmentService attachmentService;

	/**
	 * 获取附件上传界面
	 */
	@RequestMapping(value = "attachmentUpload.html", method = RequestMethod.GET)
	public String getAttachmentUploadPage(Model model) throws Exception {
		model.addAttribute("uploadUrl", qiniuUploadUrl);
		model.addAttribute("uploadToken", _generateQiniuUploadToken());
		model.addAttribute("key", StringUtils.remove(UUID.randomUUID().toString(), '-'));
		return "manage/attachmentUpload";
	}

	/**
	 * 处理上传结果
	 */
	@RequestMapping(value = "attachmentUploadResult.html", method = RequestMethod.GET)
	public String processUploadResult(@ModelAttribute("currentUser") UserInfo currentUser,
			@ModelAttribute("currentCompany") CompanyInfo currentCompany,
			@RequestParam(value = "upload_ret", required = false) String uploadRet,
			@RequestParam(value = "error", required = false) String error, HttpServletResponse response)
			throws IOException {
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();

		if (StringUtils.isNotBlank(uploadRet)) {
			System.out.println(new String(Base64.decodeBase64(uploadRet), Charsets.UTF_8));
			Map<String, Object> map = JsonMapper.NON_DEFAULT_MAPPER.fromJson(
					new String(Base64.decodeBase64(uploadRet)), JsonMapper.NON_DEFAULT_MAPPER.getMapper()
							.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class));
			AttachmentInfo attachment = new AttachmentInfo();
			attachment.setOrg(currentCompany);
			attachment.setCreateUser(currentUser);
			attachment.setMimeType((String) map.get("mimeType"));
			attachment.setName((String) map.get("name"));
			attachment.setUrl(qiniuBucketUrlPrefix + (String) map.get("key"));
			attachmentService.addNew(attachment);
			out.println("<script>window.parent.attachmentUploadCallBack('" + attachment.getId() + "','"
					+ attachment.getName() + "','" + attachment.getUrl() + "'," + attachment.isImage() + ");</script>");
		} else if (StringUtils.isNotBlank(error)) {
			logger.error("七牛文件上传出错，错误提示：" + error);
			out.println("<script>window.parent.attachmentUploadCallBack(null,'" + error + "',null,false);</script>");
		}
		out.println("<script>window.self.location.href='attachmentUpload.html';</script>");
		out.flush();
		out.close();
		return null;
	}

	// 网站域名
	@Value("#{applicationProperties['web.domain']}")
	private String webDomain;
	// 七牛配置信息
	@Value("#{applicationProperties['qiniu.upload_url']}")
	private String qiniuUploadUrl;
	@Value("#{applicationProperties['qiniu.ak']}")
	private String qiniuAk;
	@Value("#{applicationProperties['qiniu.sk']}")
	private String qiniuSk;
	@Value("#{applicationProperties['qiniu.bucket']}")
	private String qiniuBucket;
	@Value("#{applicationProperties['qiniu.bucket_url_prefix']}")
	private String qiniuBucketUrlPrefix;
	@Value("#{applicationProperties['qiniu.tokenExpirationSeconds']}")
	private int qiniuTokenExpirationSeconds;
	@Value("#{applicationProperties['qiniu.fsizeLimitInMegaByte']}")
	private int qiniuFsizeLimitInMegaByte;
	@Value("#{applicationProperties['qiniu.mimeLimit']}")
	private String qiniuMimeLimit;

	private static final String HMAC_SHA1 = "HmacSHA1";

	/**
	 * 获取七牛upload token
	 */
	private String _generateQiniuUploadToken() throws JsonProcessingException, NoSuchAlgorithmException,
			InvalidKeyException {
		Map<String, Object> map = Maps.newLinkedHashMap();
		map.put("scope", qiniuBucket);
		map.put("deadline", DateTime.now().plusSeconds(qiniuTokenExpirationSeconds).getMillis() / 1000);
		map.put("returnUrl", webDomain + "/manage/attachmentUploadResult.html");
		map.put("returnBody",
				"{\"name\": $(fname),\"key\": $(key),\"mimeType\": $(mimeType),\"size\": $(fsize),\"ext\": $(ext)}");
		map.put("fsizeLimit", qiniuFsizeLimitInMegaByte * 1024 * 1024);
		//map.put("mimeLimit", qiniuMimeLimit);

		byte[] policyBase64 = _encodeBase64Ex(JsonMapper.NON_DEFAULT_MAPPER.getMapper().writeValueAsBytes(map));

		javax.crypto.Mac mac = javax.crypto.Mac.getInstance(HMAC_SHA1);
		SecretKeySpec keySpec = new SecretKeySpec(qiniuSk.getBytes(), HMAC_SHA1);
		mac.init(keySpec);

		byte[] digest = mac.doFinal(policyBase64);
		byte[] digestBase64 = _encodeBase64Ex(digest);
		byte[] token = new byte[qiniuAk.getBytes().length + 30 + policyBase64.length];

		System.arraycopy(qiniuAk.getBytes(), 0, token, 0, qiniuAk.getBytes().length);
		token[qiniuAk.getBytes().length] = ':';
		System.arraycopy(digestBase64, 0, token, qiniuAk.getBytes().length + 1, digestBase64.length);
		token[qiniuAk.getBytes().length + 29] = ':';
		System.arraycopy(policyBase64, 0, token, qiniuAk.getBytes().length + 30, policyBase64.length);

		return new String(token);
	}

	// replace '/' with '_', '+" with '-'
	private byte[] _encodeBase64Ex(byte[] src) {
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
