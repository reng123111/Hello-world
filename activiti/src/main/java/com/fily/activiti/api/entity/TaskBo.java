/**  

* <p>Title: TaskBo.java</p>  

* <p>Description: </p>  

* <p>Copyright: Copyright (c) 2017</p>  

* <p>Company: http://www.benefitech.cn/</p>  

* @author liulin  

* @date 2018年7月11日  

* @version 1.0  

*/ 
package com.fily.activiti.api.entity;

import java.util.Map;

/**  

* <p>Title: TaskBo</p>  

* <p>Description: </p>  

* @author liulin

* @date 2018年7月11日  

*/
public class TaskBo {
	//任务ID
	private String taskId;
	//任务名称
	private String taskName;
	//审批人
	private String approveUserName;
	//启动时间
	private String startTime;
	//结束时间
	private String endTime;
	//用户任务到期日
	private String dueTime;
	//流程名称(name)
	private String processName;
	//流程实例ID
	private String processInsId;
	//流程定义ID
	private String processDefineId;
	//流程状态
	private String status;
	//流程版本
	private  int version;
	//事件ID
	private String businessEventId;
	//业务数据ID
	private String businessId;
	//表单KEY
	private String formKey;
	//持续时间
	private long duration;
	private String executionId;
	//流程启动人
	private String processStartUserName;
	//流程启动时间
	private String processStartTime;
	private Map<String,Object> processVars;
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getApproveUserName() {
		return approveUserName;
	}
	public void setApproveUserName(String approveUserName) {
		this.approveUserName = approveUserName;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getDueTime() {
		return dueTime;
	}
	public void setDueTime(String dueTime) {
		this.dueTime = dueTime;
	}

	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getBusinessEventId() {
		return businessEventId;
	}
	public void setBusinessEventId(String businessEventId) {
		this.businessEventId = businessEventId;
	}
	public String getProcessDefineId() {
		return processDefineId;
	}
	public void setProcessDefineId(String processDefineId) {
		this.processDefineId = processDefineId;
	}
	public String getProcessStartUserName() {
		return processStartUserName;
	}
	public void setProcessStartUserName(String processStartUserName) {
		this.processStartUserName = processStartUserName;
	}
	public Map<String, Object> getProcessVars() {
		return processVars;
	}
	public void setProcessVars(Map<String, Object> processVars) {
		this.processVars = processVars;
	}
	public String getProcessStartTime() {
		return processStartTime;
	}
	public void setProcessStartTime(String processStartTime) {
		this.processStartTime = processStartTime;
	}
	public String getFormKey() {
		return formKey;
	}
	public void setFormKey(String formKey) {
		this.formKey = formKey;
	}
	public String getExecutionId() {
		return executionId;
	}
	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}
	public String getProcessInsId() {
		return processInsId;
	}
	public void setProcessInsId(String processInsId) {
		this.processInsId = processInsId;
	}
	public String getBusinessId() {
		return businessId;
	}
	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
}
