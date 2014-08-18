package com.minyisoft.webapp.yjmz.common.model;

import com.minyisoft.webapp.core.model.IModelObject;

/**
 * @author qingyong_ou 工作流业务基类对象接口
 */
public interface WorkFlowBusinessModel extends IModelObject {
	/**
	 * 业务单号
	 * 
	 * @return
	 */
	String getBillNumber();

	/**
	 * 名称
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 对应工作流程实例ID
	 * 
	 * @return
	 */
	String getProcessInstanceId();
}
