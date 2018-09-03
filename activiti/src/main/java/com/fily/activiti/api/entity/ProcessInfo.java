/**  

* <p>Title: ProcessInfo.java</p>  

* <p>Description: </p>  

* <p>Copyright: Copyright (c) 2017</p>  

* <p>Company: http://www.benefitech.cn/</p>  

* @author liulin  

* @date 2018年7月17日  

* @version 1.0  

*/ 
package com.fily.activiti.api.entity;

import java.io.Serializable;
import java.util.Map;

/**  

* <p>Title: ProcessInfo</p>  

* <p>Description: </p>  

* @author liulin

* @date 2018年7月17日  

*/
public class ProcessInfo implements Serializable{
	/** serialVersionUID*/  
	private static final long serialVersionUID = 3958887669849604167L;
	private String proInsId;
	private String proDefId;
	private String proDefKey;
	private String proDeploymentId;
	private int version;
	private String processName;
	private String processDesc;
	private String status;
	private String startTime;
	private String endTime;
	private long duration;
	private String proStartUserId;
	private String currentTaskName;
	private Map<String,Object> vars;
	public String getProDefId() {
		return proDefId;
	}
	public void setProDefId(String proDefId) {
		this.proDefId = proDefId;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public String getProcessDesc() {
		return processDesc;
	}
	public void setProcessDesc(String processDesc) {
		this.processDesc = processDesc;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getProInsId() {
		return proInsId;
	}
	public void setProInsId(String proInsId) {
		this.proInsId = proInsId;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public Map<String, Object> getVars() {
		return vars;
	}
	public void setVars(Map<String, Object> vars) {
		this.vars = vars;
	}
	public String getProDefKey() {
		return proDefKey;
	}
	public void setProDefKey(String proDefKey) {
		this.proDefKey = proDefKey;
	}
	public String getProDeploymentId() {
		return proDeploymentId;
	}
	public void setProDeploymentId(String proDeploymentId) {
		this.proDeploymentId = proDeploymentId;
	}
	public String getProStartUserId() {
		return proStartUserId;
	}
	public void setProStartUserId(String proStartUserId) {
		this.proStartUserId = proStartUserId;
	}
	public String getCurrentTaskName() {
		return currentTaskName;
	}
	public void setCurrentTaskName(String currentTaskName) {
		this.currentTaskName = currentTaskName;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
}
