package com.minyisoft.webapp.yjmz.common.model;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;

import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.core.model.DataBaseInfo;
import com.minyisoft.webapp.core.model.ISystemOrgObject;

/**
 * @author qingyong_ou 附件
 */
@Getter
@Setter
@ModelKey(0x14902751715L)
public class AttachmentInfo extends DataBaseInfo {
	// 所属组织
	private ISystemOrgObject org;
	// 资源路径
	private String url;
	// 资料类型
	private String mimeType;

	/**
	 * 是否图片资源
	 * 
	 * @return
	 */
	public boolean isImage() {
		return StringUtils.startsWithIgnoreCase(mimeType, "image");
	}
}
