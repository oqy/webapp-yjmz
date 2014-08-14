package com.minyisoft.webapp.yjmz.common.util;

import com.minyisoft.webapp.core.security.utils.DigestUtils;
import com.minyisoft.webapp.core.security.utils.EncodeUtils;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.yjmz.common.model.UserInfo;
import com.minyisoft.webapp.yjmz.common.security.ShiroDbRealm;

public class CommonTool {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ObjectUuidUtils.registerModelClass(UserInfo.class);
		System.out.println(ObjectUuidUtils.createObjectID(UserInfo.class));

		String passwordSalt = EncodeUtils.encodeHex(DigestUtils.generateSalt(4));
		System.out.println(passwordSalt);
		System.out.println(ShiroDbRealm.hashPassword("123456", passwordSalt));
	}

}
