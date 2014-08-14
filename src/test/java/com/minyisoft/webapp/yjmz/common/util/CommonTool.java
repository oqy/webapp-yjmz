package com.minyisoft.webapp.yjmz.common.util;

import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.yjmz.common.model.RoleInfo;

public class CommonTool {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ObjectUuidUtils.registerModelClass(RoleInfo.class);
		System.out.println(ObjectUuidUtils.createObjectID(RoleInfo.class));
	}

}
