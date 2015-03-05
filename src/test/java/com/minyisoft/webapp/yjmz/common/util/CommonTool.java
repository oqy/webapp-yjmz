package com.minyisoft.webapp.yjmz.common.util;

import org.joda.time.DateTime;

import com.minyisoft.webapp.core.model.PermissionInfo;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;

public class CommonTool {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 获取model key
		System.out.println(Long.toHexString(DateTime.now().getMillis()).toUpperCase());

		// 获取对象ID
		ObjectUuidUtils.registerModelClass(PermissionInfo.class);
		System.out.println(ObjectUuidUtils.createObjectID(PermissionInfo.class));
	}

}
