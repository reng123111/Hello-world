/**  
* <p>Title: ProcessStatus.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2017</p>  
* <p>Company: http://www.benefitech.cn/</p>  
* @author liulin
* @date 2018年7月27日  

* @version 1.0  

*/ 
package com.fily.activiti.api.entity;

/**  
* <p>Title: ProcessStatus</p>  
* <p>Description:流程状态 </p>  
* @author liulin
* @date 2018年7月27日  
*/
public enum ProcessStatus {
	FINISHED("1","已办结"),RUNNING("2","办结中");
	private String code;
	private String name;
	private ProcessStatus(String code,String name){
		this.code=code;
		this.name=name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
}
