package com.minyisoft.webapp.yjmz.common.model;

import java.util.Date;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.yjmz.common.model.enumField.WorkFlowProcessStatusEnum;

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
	 * 获取工作流程状态
	 * 
	 * @return
	 */
	WorkFlowProcessStatusEnum getProcessStatus();

	/**
	 * 设置工作流程状态
	 * 
	 * @param status
	 */
	void setProcessStatus(WorkFlowProcessStatusEnum status);

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

	/**
	 * 获取创建时间
	 * 
	 * @return
	 */
	Date getCreateDate();
}
