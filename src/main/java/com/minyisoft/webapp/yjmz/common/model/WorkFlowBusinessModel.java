package com.minyisoft.webapp.yjmz.common.model;

import com.minyisoft.webapp.core.model.IModelObject;

/**
 * @author qingyong_ou 工作流业务基类对象接口
 */
public interface WorkFlowBusinessModel extends IModelObject {
	/**
	 * 流程实例名称
	 * 
	 * @return
	 */
	String getProcessInstanceName();

	/**
	 * 对应工作流程实例ID
	 * 
	 * @return
	 */
	String getProcessInstanceId();

	/**
	 * 设置对应流程实例ID
	 * 
	 * @return
	 */
	void setProcessInstanceId(String processInstanceId);

	/**
	 * 获取业务对象对应的工作流程变量名
	 * 
	 * @return
	 */
	String getBusinessModelProcessVariableName();

	/**
	 * 是否尚未启动工作流
	 * 
	 * @return
	 */
	boolean isProcessUnStarted();
}
