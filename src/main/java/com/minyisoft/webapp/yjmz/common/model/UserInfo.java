package com.minyisoft.webapp.yjmz.common.model;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotBlank;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.core.model.DataBaseInfo;
import com.minyisoft.webapp.core.model.ISystemUserObject;

/**
 * @author qingyong_ou 系统用户
 * 
 */
@Getter
@Setter
@ModelKey(0x147C41EBF72L)
public class UserInfo extends DataBaseInfo implements ISystemUserObject {
	@Label("用户登录账号")
	@NotBlank
	private String userLoginName;
	@Label("用户登录密码")
	@NotBlank
	private String userPassword;
	// 用户登录密码附加字符串，由系统随机生成
	private String userPasswordSalt;
	@Label("手机号码")
	@NotBlank
	private String cellPhoneNumber;
	// 会员微信OpenID
	private String weixinOpenId;
}
