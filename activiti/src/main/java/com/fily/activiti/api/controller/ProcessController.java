package com.fily.activiti.api.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.dy.springcloud.framework.web.module.AjaxResponseObject;
import com.fily.activiti.api.entity.FlowableConstants;
import com.fily.activiti.api.entity.Page;
import com.fily.activiti.api.entity.Pageable;
import com.fily.activiti.api.entity.ProcessInfo;
import com.fily.activiti.api.entity.TaskQueryBo;
import com.fily.activiti.api.service.ProcessService;
import com.fily.activiti.api.util.Tools;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 	流程相关接口
 * @author 任钢
 * 2018年8月31日
 */
@RestController
@RequestMapping("/process")
@Api("流程引擎管理相关的api")
public class ProcessController {
	
	@Autowired
	private ProcessService processService;
	
	/**
	 * <p>
	 * Title: deployProcessFile
	 * </p>
	 * 
	 * <p>
	 * Description:发布流程文件
	 * </p>
	 * 
	 * @param multipartFile
	 * @return
	 * 
	 */
	@RequestMapping(value="/deploy",method = {RequestMethod.POST})
	@ApiOperation(value="上传流程文件", notes="上传流程文件,格式为ZIP、BAR、BPMN20.XML、BPMN")
	public AjaxResponseObject deployProcessFile(@ApiParam(name = "file",required=true)@RequestParam(value = "file", required = true) MultipartFile file) {
		boolean result = processService.deployProcessSubmit(file);
		if (result) {
			return new AjaxResponseObject(true, FlowableConstants.DEPLOY_SUCCESS);
		} else {
			return new AjaxResponseObject(false, FlowableConstants.DEPLOY_FAILED);
		}
	}

	/**
	 * 
	 * <p>
	 * Title: getTodoTasks
	 * </p>
	 * 
	 * <p>
	 * Description:获取待办任务
	 * </p>
	 * 
	 * @param page
	 * @param taskQueryBo
	 * @return
	 * 
	 */
	@RequestMapping(value="/getTodoTasks",method = {RequestMethod.POST})
	@ApiOperation(value="获取待办任务", notes="获取待办任务")
	public AjaxResponseObject getTodoTasks(
			 @ApiParam(name = "pageNum", value = "当前页数",required=false)@RequestParam(required = false) Integer pageNum,
			 @ApiParam(name = "pageSize", value = "每页数据量",required=false)@RequestParam(required = false) Integer pageSize, 
			 @ApiParam(name = "assignee", value = "代理人",required=false)@RequestParam(required = false) String assignee,
			 @ApiParam(name = "candidateUser", value = "候选人",required=false)@RequestParam(required = false) String candidateUser, 
			 @ApiParam(name = "proInsId", value = "流程实例ID",required=false)@RequestParam(required = false) String proInsId,
			 @ApiParam(name = "taskId", value = "任务ID",required=false)@RequestParam(required = false) String taskId) {
		Pageable page = null;
		if (pageNum != null && pageSize != null) {
			page = new Pageable();
			page.setPageNow(pageNum);
			page.setPageSize(pageSize);
		}
		TaskQueryBo taskQueryBo = new TaskQueryBo();
		if (Tools.isNotNull(assignee))
			taskQueryBo.setAssignee(assignee);

		if (Tools.isNotNull(candidateUser))
			taskQueryBo.setCandidateUser(candidateUser);
		if (Tools.isNotNull(proInsId))
			taskQueryBo.setProInsId(proInsId);
		if (Tools.isNotNull(taskId))
			taskQueryBo.setTaskId(taskId);
		Page tasks = null;
		try {
			tasks = processService.getCurrentWorkList(page, taskQueryBo);
		} catch (Exception e) {
			return new AjaxResponseObject(false, FlowableConstants.QUERY_FIALED, e.getMessage());
		}
		return new AjaxResponseObject(true, FlowableConstants.QUERY_SUCCESS, tasks);
	}

	/**
	 * 
	 * <p>
	 * Title: getDoneTasks
	 * </p>
	 * 
	 * <p>
	 * Description: 获取已办任务
	 * </p>
	 * 
	 * @param page
	 * @param taskQueryBo
	 * @return
	 * 
	 */
	@RequestMapping(value="/getDoneTasks",method = {RequestMethod.POST})
	@ApiOperation(value="获取已办任务", notes="获取已办任务")
	public AjaxResponseObject getDoneTasks(
			@ApiParam(name = "pageNum", value = "当前页数",required=false)@RequestParam(required = false) Integer pageNum,
			@ApiParam(name = "pageSize", value = "每页数据量",required=false)@RequestParam(required = false) Integer pageSize,
			@ApiParam(name = "assignee", value = "代理人",required=false)@RequestParam(required = false) String assignee,
			@ApiParam(name = "candidateUser", value = "候选人",required=false)@RequestParam(required = false) String candidateUser,
			@ApiParam(name = "proInsId", value = "流程实例ID",required=false)@RequestParam(required = false) String proInsId,
			@ApiParam(name = "taskId", value = "任务ID",required=false)@RequestParam(required = false) String taskId) {
		Pageable page = null;
		if (pageNum != null && pageSize != null) {
			page = new Pageable();
			page.setPageNow(pageNum);
			page.setPageSize(pageSize);
		}
		TaskQueryBo taskQueryBo = new TaskQueryBo();
		if (Tools.isNotNull(assignee))
			taskQueryBo.setAssignee(assignee);

		if (Tools.isNotNull(candidateUser))
			taskQueryBo.setCandidateUser(candidateUser);
		if (Tools.isNotNull(proInsId))
			taskQueryBo.setProInsId(proInsId);
		if (Tools.isNotNull(taskId))
			taskQueryBo.setTaskId(taskId);
		Page tasks =null;
		try {
			tasks = processService.getHistoryWorkList(page, taskQueryBo);
		} catch (Exception e) {
			return new AjaxResponseObject(false, FlowableConstants.QUERY_FIALED, e.getMessage());
		}
		return new AjaxResponseObject(true, FlowableConstants.QUERY_SUCCESS, tasks);
	}

	/**
	 * 
	 * <p>
	 * Title: viewImage
	 * </p>
	 * 
	 * <p>
	 * Description:获取流程图
	 * </p>
	 * 
	 * @param processDefinitionId
	 * @param resourceName
	 * @param pProcessInstanceId
	 * @param response
	 * @throws Exception
	 * 
	 */
	@RequestMapping(value = "/viewImage",method = {RequestMethod.POST})
	@ApiOperation(value="获取流程图", notes="获取流程图")
	public void viewImage(
			@ApiParam(name = "processDefinitionId", value = "流程定义ID",required=false)@RequestParam(required = false)String processDefinitionId, 
			@ApiParam(name = "processInstanceId", value = "流程实例ID",required=false)@RequestParam(required = false)String processInstanceId,
			HttpServletResponse response)
			throws Exception {
		// 设置页面不缓存
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/png");
		processService.viewImage(processDefinitionId, processInstanceId, response);
	}
	@RequestMapping(value = "/getImageStr",method = {RequestMethod.POST})
	@ApiOperation(value="获取流程图", notes="FeginClient接口调用,返回流程图字符串，BASE64加密")
	public AjaxResponseObject getImageStr(
			@ApiParam(name = "processDefinitionId", value = "流程定义ID",required=false)@RequestParam(required = false)String processDefinitionId, 
			@ApiParam(name = "processInstanceId", value = "流程实例ID",required=false)@RequestParam(required = false)String processInstanceId)
			throws Exception {
		String imageStr=null;
		try {
			imageStr = processService.getImageStr(processDefinitionId, processInstanceId);
		} catch (Exception e) {
			return new AjaxResponseObject(false,FlowableConstants.QUERY_FIALED,e.getMessage());
		}
		return new AjaxResponseObject(true,FlowableConstants.QUERY_SUCCESS,imageStr);
	}

	/**
	 * 
	 * <p>
	 * Title: listProcessDefinition
	 * </p>
	 * 
	 * <p>
	 * Description:查询流程定义
	 * </p>
	 * 
	 * @param page
	 * @return
	 * 
	 */
	@ApiOperation(value="查询流程定义", notes="查询流程定义")
	@RequestMapping(value = "/listProcessDefinition",method = {RequestMethod.POST})
	public AjaxResponseObject listProcessDefinition(
			@ApiParam(name = "pageNum", value = "当前页数",required=false)@RequestParam(required = false) Integer pageNum, 
			@ApiParam(name = "pageSize", value = "每页数据量",required=false)@RequestParam(required = false) Integer pageSize) {
		Pageable page = null;
		if (pageNum != null && pageSize != null) {
			page = new Pageable();
			page.setPageNow(pageNum);
			page.setPageSize(pageSize);
		}
		Page processes = processService.findProcessDefinition(page);
		return new AjaxResponseObject(true, FlowableConstants.OPER_SUCCESS, processes);
	}

	/**
	 * 
	 * <p>
	 * Title: startProcess
	 * </p>
	 * 
	 * <p>
	 * Description: 启动流程
	 * </p>
	 * 
	 * @param processDefineId
	 *            流程定义ID
	 * @param vars
	 *            流程参数
	 * @return
	 * 
	 */
	@RequestMapping(value = "/startProcess",method = {RequestMethod.POST})
	@ApiOperation(value="启动流程", notes="启动流程")
//	@ApiImplicitParams( {
//		@ApiImplicitParam(name="processDefineKey", value="流程key"),
//		@ApiImplicitParam(name="loginUserName", value="登录用户", required=true),
//		@ApiImplicitParam(name = "vars", value = "流程参数", required = true)
//	})
	
	public AjaxResponseObject startProcess(@RequestParam(required=false) String processDefineKey, String loginUserName, String params) {
//		String processDefineKey = (String) vars.get("processDefineKey");
		if (Tools.isNull(processDefineKey)) {
			return new AjaxResponseObject(false, FlowableConstants.PROCKEY_IS_NULL);
		}
//		String loginUserName = (String) vars.get("loginUserName");
		if (Tools.isNull(loginUserName)) {
			return new AjaxResponseObject(false, FlowableConstants.LOGIN_USER_IS_NULL);
		}
		
		Map<String, Object> vars = new HashMap();
		if(Tools.isNotNull(params)) {
			vars = JSONObject.parseObject(params);
		}
		
		String businessKey = (String) vars.get("businessKey");
		if (Tools.isNotNull(businessKey)) {
			vars.remove("businessKey");
		}
		ProcessInfo processInfo = null;
		try {
			processInfo = processService.startProcess(processDefineKey, businessKey, loginUserName, vars);
		} catch (Exception e) {
			return new AjaxResponseObject(false, FlowableConstants.OPER_FAILED, e.getMessage());
		}
		return new AjaxResponseObject(true, FlowableConstants.OPER_SUCCESS, processInfo);
	}

	/**
	 * <p>
	 * Title: commitTasks
	 * </p>
	 * <p>
	 * Description: 提交任务
	 * </p>
	 * 
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/commitTasks",method = {RequestMethod.POST})
	@ApiOperation(value="提交任务", notes="提交任务")
	@ApiImplicitParam(name = "vars", value = "任务参数,至少包括任务id:taskId,登录用户ID：loginUserId", required = true, dataType = "Map")
	public AjaxResponseObject commitTasks(@RequestBody Map<String, Object> vars) {
		String taskId = (String) vars.get("taskId");
		if (Tools.isNull(taskId)) {
			return new AjaxResponseObject(false, FlowableConstants.TASK_ID_IS_NULL);
		}
		vars.remove("taskId");
		String loginUserId = (String) vars.get("loginUserId");
		if(Tools.isNull(loginUserId)){
			return new AjaxResponseObject(false, FlowableConstants.LOGIN_USER_IS_NULL);
		}
		vars.remove("loginUserId");
		try {
			processService.completeTask(taskId,loginUserId, vars);
		} catch (Exception e) {
			return new AjaxResponseObject(false, FlowableConstants.OPER_FAILED, e.getMessage());
		}
		return new AjaxResponseObject(true, FlowableConstants.OPER_SUCCESS);
	}

	/**
	 * <p>
	 * Title: queryMyProcesses
	 * </p>
	 * <p>
	 * Description:查询我的发起流程
	 * </p>
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/queryMyProcesses",method={RequestMethod.POST})
	@ApiOperation(value="查询用户发起的流程", notes="查询用户发起的流程")
	public AjaxResponseObject queryMyProcesses(
			@ApiParam(name = "userId", value = "用户ID",required=true)@RequestParam(value = "userId", required = true) String userId,
			@ApiParam(name = "pageNum", value = "当前页数",required=false)@RequestParam(required = false) Integer pageNum, 
			@ApiParam(name = "pageSize", value = "每页数据量",required=false)@RequestParam(required = false) Integer pageSize) {
		Pageable page = null;
		if (pageNum != null && pageSize != null) {
			page = new Pageable();
			page.setPageNow(pageNum);
			page.setPageSize(pageSize);
		}
		Page data = processService.findMyProcesses(userId, page);
		return new AjaxResponseObject(true, FlowableConstants.OPER_SUCCESS, data);
	}
	/**  
	 * <p>Title: deleteProcessDefinition</p>  
	 * <p>Description:删除流程定义（未启动） </p>  
	 * @param proDefId 流程定义ID
	 * @return   AjaxResponseObject
	 */  
	@RequestMapping(value = "/deleteProcessDefinition",method={RequestMethod.POST})
	@ApiOperation(value="删除流程定义", notes="通过流程定义ID进行删除")
	public AjaxResponseObject deleteProcessDefinition(@ApiParam(name = "proDefId", value = "流程定义ID",required=true)@RequestParam(value = "proDefId", required = true) String proDefId) {
		try {
			processService.deleteProcessDefinition(proDefId);
			return new AjaxResponseObject(true, FlowableConstants.OPER_SUCCESS);
		} catch (Exception e) {
			return new AjaxResponseObject(true, FlowableConstants.OPER_FAILED,e.getMessage());
		}
	}
	
	/**  
	 * <p>Title: listUserInvolvedProcess</p>  
	 * <p>Description: 查询用户参与的流程</p>  
	 * @param userId 用户ID
	 * @param processStatus 流程状态(1:已办结,2:未办结)
	 * @param pageNum 当前页数
	 * @param pageSize 每页条数
	 * @return   AjaxResponseObject
	 */  
	@RequestMapping(value = "/listUserInvolvedProcess",method={RequestMethod.POST})
	@ApiOperation(value="查询用户参与的流程", notes="查询用户参与的流程")
	public AjaxResponseObject listUserInvolvedProcess(
			@ApiParam(name = "userId", value = "用户ID",required=true)@RequestParam(required = true)String userId, 
			@ApiParam(name = "processStatus", value = "流程状态",required=false)@RequestParam(required = false) String processStatus,
			@ApiParam(name = "pageNum", value = "当前页数",required=false)@RequestParam(required = false) Integer pageNum, 
			@ApiParam(name = "pageSize", value = "每页数据量",required=false)@RequestParam(required = false) Integer pageSize) {
		Pageable page = null;
		if (pageNum != null && pageSize != null) {
			page = new Pageable();
			page.setPageNow(pageNum);
			page.setPageSize(pageSize);
		}
		Page processes=null;
		try {
			processes = processService.queryUserInvolvedProcesses(userId, page, processStatus);
			return new AjaxResponseObject(true, FlowableConstants.OPER_SUCCESS, processes);
		} catch (Exception e) {
			return new AjaxResponseObject(false, FlowableConstants.OPER_FAILED,e.getMessage());
		}
	
	}

}
