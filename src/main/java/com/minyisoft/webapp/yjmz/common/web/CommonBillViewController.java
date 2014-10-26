package com.minyisoft.webapp.yjmz.common.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.core.web.BaseController;
import com.minyisoft.webapp.yjmz.common.model.MessageInfo;
import com.minyisoft.webapp.yjmz.common.model.WorkFlowBusinessModel;

/**
 * @author qingyong_ou 查看单据详情controller
 */
@Controller
public class CommonBillViewController extends BaseController {
	// 网站域名
	@Value("#{applicationProperties['web.domain']}")
	private String webDomain;

	/**
	 * 查看单据详情，若通过微信客户端进入，通过检查微信ticket可实现已绑定微信用户的自动登录
	 */
	@RequestMapping(value = "/viewDetail.html", method = RequestMethod.GET)
	public String viewDetail(@RequestParam(value = "billId", required = false) String billId) {
		if (StringUtils.isNotBlank(billId)) {
			IModelObject modelObject = ObjectUuidUtils.getObject(billId);
			if (modelObject instanceof WorkFlowBusinessModel) {
				return "redirect:" + webDomain + "/manage/workFlowDetail.html?workFlowModelId=" + billId;
			} else if (modelObject instanceof MessageInfo) {
				return "redirect:" + webDomain + "/manage/messageReceiveList.html";
			}
		}
		return "redirect:" + webDomain + "/manage/index.html";
	}
}
