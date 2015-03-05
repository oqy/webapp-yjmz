package com.minyisoft.webapp.yjmz.common.util;

import com.minyisoft.webapp.core.utils.spring.SpringUtils;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.service.UserService;

/**
 * @author qingyong_ou 系统常量
 */
public final class SystemConstant {
	private SystemConstant() {

	}

	/**
	 * ID分隔符
	 */
	public final static String ID_SEPARATOR = "|";
	/**
	 * 系统管理员预设ID
	 */
	public final static String ADMINISTATOR_USER_ID = "AAABR8Qev3KEgi7aew1G8JUc2qtHfXML";

	/**
	 * 获取系统管理员用户对象
	 * 
	 * @return
	 */
	public final static UserInfo ADMINISTRATOR_USER = ((UserService) SpringUtils.getBean("userService"))
			.getValue(ADMINISTATOR_USER_ID);
}
