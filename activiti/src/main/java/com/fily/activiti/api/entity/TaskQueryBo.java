/**  

* <p>Title: TaskQueryBo.java</p>  

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

* <p>Title: TaskQueryBo</p>  

* <p>Description: </p>  

* @author liulin

* @date 2018年7月11日  

*/
public class TaskQueryBo{
	//候选人
	private String candidateUser;
	//代理人
	private String assignee;
	//流程变量
	private Map<String,Object> variableMap;
	//Only select tasks with the given taskDefinitionKey. The task definition key is the id of the userTask: <userTask id="xxx" .../>
	private String userTaskId;
	//任务ID
	private String taskId;
	//流程实例ID
	private String proInsId;
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public String getCandidateUser() {
		return candidateUser;
	}
	public void setCandidateUser(String candidateUser) {
		this.candidateUser = candidateUser;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getUserTaskId() {
		return userTaskId;
	}
	public void setUserTaskId(String userTaskId) {
		this.userTaskId = userTaskId;
	}
	public Map<String, Object> getVariableMap() {
		return variableMap;
	}
	public void setVariableMap(Map<String, Object> variableMap) {
		this.variableMap = variableMap;
	}
	public String getProInsId() {
		return proInsId;
	}
	public void setProInsId(String proInsId) {
		this.proInsId = proInsId;
	}
	@Override
	public String toString() {
		return "TaskQueryBo [candidateUser=" + candidateUser + ", assignee=" + assignee + ", variableMap=" + variableMap
				+ ", userTaskId=" + userTaskId + ", taskId=" + taskId + ", proInsId=" + proInsId + "]";
	}
	
}
