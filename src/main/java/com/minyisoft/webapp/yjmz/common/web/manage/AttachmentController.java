package com.minyisoft.webapp.yjmz.common.web.manage;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
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
import com.minyisoft.webapp.core.utils.mapper.json.JsonMapper;
import com.minyisoft.webapp.yjmz.common.model.AttachmentInfo;
import com.minyisoft.webapp.yjmz.common.model.CompanyInfo;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.service.AttachmentService;
import com.minyisoft.webapp.yjmz.common.util.qiniu.QiniuConstant;
import com.minyisoft.webapp.yjmz.common.util.qiniu.QiniuHelper;

/**
 * @author qingyong_ou 附件上传controller
 */
@Controller
@RequestMapping("/manage")
public class AttachmentController extends ManageBaseController {
	@Autowired
	private AttachmentService attachmentService;
	@Value("#{applicationProperties['web.domain']}")
	private String webDomain;// 网站域名

	/**
	 * 获取附件上传界面
	 */
	@RequestMapping(value = "attachmentUpload.html", method = RequestMethod.GET)
	public String getAttachmentUploadPage(Model model) throws Exception {
		model.addAttribute("uploadUrl", QiniuConstant.UPLOAD_URL);
		model.addAttribute("uploadToken", _generateQiniuUploadToken());
		model.addAttribute("key", StringUtils.remove(UUID.randomUUID().toString(), '-'));
		// 上传文件尺寸最大值最大
		model.addAttribute("sizeLimit", QiniuConstant.FSIZE_LIMIT_IN_MEGABYTE);
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
			String decodeUploadRet = new String(Base64.decodeBase64(uploadRet), Charsets.UTF_8);
			logger.info("七牛附件上传结果：" + decodeUploadRet);
			Map<String, Object> map = JsonMapper.NON_DEFAULT_MAPPER.fromJson(
					decodeUploadRet,
					JsonMapper.NON_DEFAULT_MAPPER.getMapper().getTypeFactory()
							.constructMapType(HashMap.class, String.class, Object.class));
			AttachmentInfo attachment = new AttachmentInfo();
			attachment.setOrg(currentCompany);
			attachment.setCreateUser(currentUser);
			attachment.setMimeType((String) map.get("mimeType"));
			attachment.setName((String) map.get("name"));
			attachment.setUrl(QiniuConstant.BUCKET_URL_PREFIX + (String) map.get("key"));
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

	private static final String _RETURN_BODY = "{\"name\": $(fname),\"key\": $(key),\"mimeType\": $(mimeType),\"size\": $(fsize),\"ext\": $(ext)}";

	/**
	 * 获取七牛upload token
	 */
	private String _generateQiniuUploadToken() throws JsonProcessingException, NoSuchAlgorithmException,
			InvalidKeyException {
		return QiniuHelper.generateQiniuUploadToken(webDomain + "/manage/attachmentUploadResult.html", _RETURN_BODY);
	}
}
