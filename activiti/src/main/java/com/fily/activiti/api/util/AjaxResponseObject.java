package com.fily.activiti.api.util;

import java.io.Serializable;

public class AjaxResponseObject implements Serializable{

	  private static final long serialVersionUID = 1L;
	  private Boolean success;
	  private Boolean autoPrompt = Boolean.valueOf(true);
	  
	  private String message;
	  
	  private Object content;
	  
	  private String errCode = "-1";
	  
	  public AjaxResponseObject() {}
	  
	  public AjaxResponseObject(boolean success, String message)
	  {
	    this.success = Boolean.valueOf(success);
	    this.message = message;
	  }
	  
	  public AjaxResponseObject(boolean success, String message, Boolean autoPrompt) {
	    this.success = Boolean.valueOf(success);
	    this.message = message;
	    this.autoPrompt = autoPrompt;
	  }
	  
	  public AjaxResponseObject(boolean success, Object content) {
	    this.success = Boolean.valueOf(success);
	    this.content = content;
	  }
	  
	  public AjaxResponseObject(boolean success, String message, Object content) {
	    this.success = Boolean.valueOf(success);
	    this.message = message;
	    this.content = content;
	  }
	  
	  public AjaxResponseObject(boolean success, String message, Object content, Boolean autoPrompt) {
	    this.success = Boolean.valueOf(success);
	    this.message = message;
	    this.content = content;
	    this.autoPrompt = autoPrompt;
	  }
	  
	  public AjaxResponseObject(boolean success, String message, String errCode, Object content) {
	    this.success = Boolean.valueOf(success);
	    this.message = message;
	    this.content = content;
	    this.errCode = errCode;
	  }
	  
	  public Boolean getSuccess() {
	    return this.success;
	  }
	  
	  public void setSuccess(Boolean success) {
	    this.success = success;
	  }
	  
	  public String getMessage() {
	    return this.message == null ? "" : this.message;
	  }
	  
	  public void setMessage(String message) {
	    this.message = message;
	  }
	  
	  public Object getContent() {
	    return this.content;
	  }
	  
	  public void setContent(Object content) {
	    this.content = content;
	  }
	  
	  public Boolean getAutoPrompt() {
	    return this.autoPrompt;
	  }
	  
	  public void setAutoPrompt(Boolean autoPrompt) {
	    this.autoPrompt = autoPrompt;
	  }
	  
	  public String getErrCode() {
	    return this.errCode;
	  }
	  
	  public void setErrCode(String errCode) {
	    this.errCode = errCode;
	  }

}
