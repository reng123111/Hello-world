package com.fily.activiti.api.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.zip.ZipInputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Gateway;
import org.activiti.bpmn.model.ParallelGateway;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.ReceiveTask;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.Task;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.TaskQuery;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dy.springcloud.framework.utils.StringUtil;
import com.fily.activiti.api.entity.Page;
import com.fily.activiti.api.entity.Pageable;
import com.fily.activiti.api.entity.ProcessInfo;
import com.fily.activiti.api.entity.ProcessStatus;
import com.fily.activiti.api.entity.TaskBo;
import com.fily.activiti.api.entity.TaskQueryBo;
import com.fily.activiti.api.util.CollectionUtils;
import com.fily.activiti.api.util.Tools;

/**
 * 流程业务处理类
 * @author 任钢
 * 2018年8月31日
 */
@Service
public class ProcessService {
	
	public static final Logger log = LoggerFactory.getLogger(ProcessService.class);
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private IdentityService identityService;
	@Resource
	private TaskService taskService;
	@Resource
	private HistoryService historyService;
	@Resource
	private ProcessEngine processEngine;
	@Resource
	private RepositoryService repositoryService;
/*	@Resource
	private FormService formService;*/

	/**
	 * @date 2018/3/23 16:41
	 * @title getNextNode
	 * @description: 获取下一步用户任务节点
	 * @param procDefId
	 *            流程定义ID
	 * @param taskDefKey
	 *            当前任务KEY
	 * @param map
	 *            业务数据
	 * @return: java.util.List<org.activiti.bpmn.model.UserTask>
	 */
	public List<UserTask> getNextNode(String procDefId, String taskDefKey, Map<String, Object> map) {
		List<UserTask> userTasks = new ArrayList<UserTask>();
		// 获取BpmnModel对象
		BpmnModel bpmnModel = processEngine.getRepositoryService().getBpmnModel(procDefId);
		// 获取Process对象
		Process process = bpmnModel.getProcesses().get(bpmnModel.getProcesses().size() - 1);
		// 获取所有的FlowElement信息
		Collection<FlowElement> flowElements = process.getFlowElements();
		// 获取当前节点信息
		FlowElement flowElement = getFlowElementById(taskDefKey, flowElements);

		getNextNode(flowElements, flowElement, map, userTasks);

		return userTasks;
	}

	/**
	 * @date 2018/4/11 9:52
	 * @title getNextNode
	 * @description: 查询下一步用户节点
	 * @param flowElements
	 *            全流程节点集合
	 * @param flowElement
	 *            当前节点
	 * @param map
	 *            业务数据
	 * @param nextUser
	 *            下一步用户节点
	 * @return: void
	 */
	public void getNextNode(Collection<FlowElement> flowElements, FlowElement flowElement, Map<String, Object> map,
			List<UserTask> nextUser) {
		// 如果是结束节点
		if (flowElement instanceof EndEvent) {
			// 如果是子任务的结束节点
			if (getSubProcess(flowElements, flowElement) != null) {
				flowElement = getSubProcess(flowElements, flowElement);
			}
		}
		// 获取Task的出线信息--可以拥有多个
		List<SequenceFlow> outGoingFlows = null;
		if (flowElement instanceof Task) {
			outGoingFlows = ((Task) flowElement).getOutgoingFlows();
		} else if (flowElement instanceof Gateway) {
			outGoingFlows = ((Gateway) flowElement).getOutgoingFlows();
		} else if (flowElement instanceof StartEvent) {
			outGoingFlows = ((StartEvent) flowElement).getOutgoingFlows();
		} else if (flowElement instanceof SubProcess) {
			outGoingFlows = ((SubProcess) flowElement).getOutgoingFlows();
		}
		if (outGoingFlows != null && outGoingFlows.size() > 0) {
			// 遍历所有的出线--找到可以正确执行的那一条
			for (SequenceFlow sequenceFlow : outGoingFlows) {
				// 1.有表达式，且为true
				// 2.无表达式
				String expression = sequenceFlow.getConditionExpression();
				if (StringUtils.isEmpty(expression) || Boolean.valueOf(String.valueOf(FelSupport.result(map,
						expression.substring(expression.lastIndexOf("{") + 1, expression.lastIndexOf("}")))))) {
					// 出线的下一节点
					String nextFlowElementID = sequenceFlow.getTargetRef();
					// 查询下一节点的信息
					FlowElement nextFlowElement = getFlowElementById(nextFlowElementID, flowElements);

					// 用户任务
					if (nextFlowElement instanceof UserTask) {
						nextUser.add((UserTask) nextFlowElement);
					}
					// 排他网关
					else if (nextFlowElement instanceof ExclusiveGateway) {
						getNextNode(flowElements, nextFlowElement, map, nextUser);
					}
					// 并行网关
					else if (nextFlowElement instanceof ParallelGateway) {
						getNextNode(flowElements, nextFlowElement, map, nextUser);
					}
					// 接收任务
					else if (nextFlowElement instanceof ReceiveTask) {
						getNextNode(flowElements, nextFlowElement, map, nextUser);
					}
					// 子任务的起点
					else if (nextFlowElement instanceof StartEvent) {
						getNextNode(flowElements, nextFlowElement, map, nextUser);
					}
					// 结束节点
					else if (nextFlowElement instanceof EndEvent) {
						getNextNode(flowElements, nextFlowElement, map, nextUser);
					}
				}
			}
		}
	}

	/**
	 * @date 2018/4/11 9:44
	 * @title getSubProcess
	 * @description: 查询一个节点的是否子任务中的节点，如果是，返回子任务
	 * @param flowElements
	 *            全流程的节点集合
	 * @param flowElement
	 *            当前节点
	 * @return: org.activiti.bpmn.model.FlowElement
	 */
	public FlowElement getSubProcess(Collection<FlowElement> flowElements, FlowElement flowElement) {
		for (FlowElement flowElement1 : flowElements) {
			if (flowElement1 instanceof SubProcess) {
				for (FlowElement flowElement2 : ((SubProcess) flowElement1).getFlowElements()) {
					if (flowElement.equals(flowElement2)) {
						return flowElement1;
					}
				}
			}
		}
		return null;
	}

	/**
	 * @date 2018/3/23 16:43
	 * @title getFlowElementById
	 * @description: 根据ID查询流程节点对象,如果是子任务，则返回子任务的开始节点
	 * @param Id
	 *            节点ID
	 * @param flowElements
	 *            流程节点集合
	 * @return: org.activiti.bpmn.model.FlowElement
	 */
	public FlowElement getFlowElementById(String Id, Collection<FlowElement> flowElements) {
		for (FlowElement flowElement : flowElements) {
			if (flowElement.getId().equals(Id)) {
				// 如果是子任务，则查询出子任务的开始节点
				if (flowElement instanceof SubProcess) {
					return getStartFlowElement(((SubProcess) flowElement).getFlowElements());
				}
				return flowElement;
			}
			if (flowElement instanceof SubProcess) {
				FlowElement flowElement1 = getFlowElementById(Id, ((SubProcess) flowElement).getFlowElements());
				if (flowElement1 != null) {
					return flowElement1;
				}
			}
		}
		return null;
	}

	/**
	 * @date 2018/4/10 15:57
	 * @title getStartFlowElement
	 * @description: 返回流程的开始节点
	 * @param flowElements
	 * @return: org.activiti.bpmn.model.FlowElement
	 */
	public FlowElement getStartFlowElement(Collection<FlowElement> flowElements) {
		for (FlowElement flowElement : flowElements) {
			if (flowElement instanceof StartEvent) {
				return flowElement;
			}
		}
		return null;
	}
	
	public boolean deployProcessSubmit(MultipartFile processFile) {
		try {
			// 得到输入流（字节流）对象
			InputStream fileInputStream = processFile.getInputStream();
			String fileName = processFile.getOriginalFilename();
			// 文件扩展名
			String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
			// zip或者bar类型的文件用ZipInputStream方式部署
			DeploymentBuilder deployment = repositoryService.createDeployment();
			if (extension.equals("zip") || extension.equals("bar")) {
				ZipInputStream zip = new ZipInputStream(fileInputStream);
				deployment.addZipInputStream(zip);
			} else if(extension.equals("xml") || extension.equals("bpmn") ) {
				// xml、bpmn类型的文件
				deployment.addInputStream(fileName, fileInputStream);
			}else{
				log.info("目前只支持ZIP、BAR、BPMN、BPMN20.XML文件格式!");
				return false;
			}
			deployment.deploy();
		} catch (Exception e) {
			log.error("上传流程文件失败", e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @User :Test
	 * @date :2014-6-27 上午09:53:09
	 * @return :List
	 * @userFor :获得待办任务列表
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Page getCurrentWorkList(Pageable page, TaskQueryBo taskQueryBo) {
		List<org.activiti.engine.task.Task> tasks = new ArrayList<org.activiti.engine.task.Task>();
		// 封装查询对象
		TaskQuery taskQuery = getTaskQuery(taskQueryBo);
		// 设置排序
		taskQuery.orderByTaskPriority().desc();
		taskQuery.orderByTaskCreateTime().desc();
		List<org.activiti.engine.task.Task> todoList = null;
		// 获取数据
		if(page != null){
			int start = (page.getPageNow() - 1) * page.getPageSize();
			int limit = page.getPageSize();
			todoList=taskQuery.listPage(start, limit);
		}else{
			todoList=taskQuery.list();
		}
		tasks.addAll(todoList);
		List<TaskBo> taskBoList = new ArrayList<TaskBo>();
		TaskBo taskBo = null;
		// TaskQuery转换成Task
		for (Iterator iterator = tasks.iterator(); iterator.hasNext(); taskBoList.add(taskBo)) {
			org.activiti.engine.task.Task task = (org.activiti.engine.task.Task) iterator.next();
			taskBo = getTaskDetail(task.getId());
		}
		if(page != null){
			return new Page(new Pageable(taskQuery.count(), page.getPageNow()), taskBoList);
		}else{
			return new Page(taskBoList);
		}
	}

	/**
	 * 
	 * @User :Test
	 * @date :2014-6-27 上午09:55:12
	 * @return :TaskQuery
	 * @userFor :根据不同的条件获得任务列表 TaskQuery
	 */
	private TaskQuery getTaskQuery(TaskQueryBo taskQueryBo) {
		TaskQuery taskQuery = taskService.createTaskQuery();
		if (taskQueryBo != null) {
			if(StringUtil.isNotBlank(taskQueryBo.getProInsId())){
				taskQuery.processInstanceId(taskQueryBo.getProInsId());
			}
			String candidateUser = taskQueryBo.getCandidateUser();
			if (StringUtil.isNotBlank(candidateUser))
				taskQuery = taskQuery.taskCandidateUser(candidateUser);
			Map<String, Object> variableMap = taskQueryBo.getVariableMap();
			if (variableMap != null && variableMap.size() > 0) {
				Set<Entry<String, Object>> set = variableMap.entrySet();
				for (Entry<String, Object> entry : set) {
					taskQuery = taskQuery.processVariableValueEquals(entry.getKey(), entry.getValue());
				}
			}
			String userTaskId = taskQueryBo.getUserTaskId();
			if (StringUtil.isNotBlank(userTaskId))
				taskQuery = taskQuery.taskDefinitionKey(userTaskId);
			String taskId = taskQueryBo.getTaskId();
			if (StringUtil.isNotBlank(taskId))
				taskQuery = taskQuery.taskId(taskId);
			String assignee = taskQueryBo.getAssignee();
			if (StringUtil.isNotBlank(assignee))
				taskQuery = taskQuery.taskAssignee(assignee);
		}
		return taskQuery;
	}

	private HistoricTaskInstanceQuery getHistoricTaskQuery(TaskQueryBo taskQueryBo) {
		HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery();
		if (taskQueryBo != null) {
			String candidateUser = taskQueryBo.getCandidateUser();
			if (StringUtil.isNotBlank(candidateUser))
				query = query.taskCandidateUser(candidateUser);
			String assignee = taskQueryBo.getAssignee();
			if (StringUtil.isNotBlank(assignee))
				query = query.taskAssignee(assignee);
			String taskId = taskQueryBo.getTaskId();
			if (StringUtil.isNotBlank(taskId))
				query = query.taskId(taskId);
			if(StringUtil.isNotBlank(taskQueryBo.getProInsId())){
				query.processInstanceId(taskQueryBo.getProInsId());
			}
			query.finished();
		}
		return query;
	}

	/**
	 * 
	 * <p>
	 * Title: getTaskDetail
	 * </p>
	 * 
	 * <p>
	 * Description:获取任务详情
	 * </p>
	 * 
	 * @param taskId
	 * @return
	 * 
	 */
	public TaskBo getTaskDetail(String taskId) {
		TaskBo taskBo = null;
		HistoricTaskInstance historicTaskInstance = (HistoricTaskInstance) historyService
				.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
		if (historicTaskInstance != null)
			taskBo = setTaskBo(historicTaskInstance);
		return taskBo;
	}

	/**
	 * 
	 * @User :Test
	 * @date :2014-6-27 上午09:59:45
	 * @return :TaskBo
	 * @userFor :设置页面需要的字段信息 HistoricTaskInstance
	 */
	private TaskBo setTaskBo(HistoricTaskInstance task) {
		TaskBo taskBo = new TaskBo();
		taskBo.setTaskId(task.getId());
		taskBo.setTaskName(task.getName());
		taskBo.setApproveUserName(task.getAssignee());
		taskBo.setStartTime(Tools.formatDateToStr(0, task.getStartTime()));
		if (task.getEndTime() == null)
			taskBo.setEndTime(Tools.formatDateToStr(0, task.getEndTime()));
		else
			taskBo.setEndTime("");
		taskBo.setDueTime(Tools.formatDateToStr(0, task.getDueDate()));
		ProcessDefinition processDefinition = (ProcessDefinition) repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(task.getProcessDefinitionId()).singleResult();
		taskBo.setProcessName(processDefinition.getName());
		taskBo.setProcessDefineId(processDefinition.getId());
		//taskBo.setStatus(processDefinition.isSuspended() ? "已挂起" : "正常");
		taskBo.setVersion(Integer.valueOf(processDefinition.getVersion()));
		taskBo.setFormKey(task.getFormKey());
		taskBo.setExecutionId(task.getExecutionId());
		taskBo.setApproveUserName(getTaskCandidate(task.getId()));
		HistoricProcessInstance historicProcessInstance = (HistoricProcessInstance) historyService
				.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
		// 得到业务id即eventid
		taskBo.setBusinessEventId(historicProcessInstance.getBusinessKey());
		// 发起人姓名
		if (historicProcessInstance.getStartUserId() != null)
			taskBo.setProcessStartUserName(historicProcessInstance.getStartUserId());
		else
			taskBo.setProcessStartUserName("");
		taskBo.setProcessStartTime(Tools.formatDateToStr(0,historicProcessInstance.getStartTime()));
		// 流程状态
		if(historicProcessInstance.getEndTime() != null)
			taskBo.setStatus(ProcessStatus.FINISHED.getName());
		else
			taskBo.setStatus(ProcessStatus.RUNNING.getName());
		taskBo.setProcessInsId(historicProcessInstance.getId());
		//业务数据ID
		if(historicProcessInstance.getBusinessKey() != null){
			taskBo.setBusinessId(historicProcessInstance.getBusinessKey());	
		}
		taskBo.setFormKey(task.getFormKey());
		/*//获取运行流程变量
		Map<String,Object> variables=taskService.getVariables(task.getId());
		taskBo.setProcessVars(variables);*/
		// 获取历史流程变量
		List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery()
				.processInstanceId(historicProcessInstance.getId()).list();
		if (varInstanceList != null && varInstanceList.size() > 0) {
			Map<String, Object> varMap = new HashMap<String, Object>();
			for (HistoricVariableInstance var : varInstanceList) {
				varMap.put(var.getVariableName(), var.getValue());
			}
			taskBo.setProcessVars(varMap);
		}
		
		return taskBo;
	}
	/**
     * 
     *@User   :Test
     *@date   :2014-6-27 上午09:38:36
     *@return :Set
     *@userFor :获得任务中的办理候选人
     */
	private String getTaskCandidate(String taskId) {
		Set<String> users=new HashSet<String>();
		List identityLinkList = taskService.getIdentityLinksForTask(taskId);
		if (identityLinkList != null && identityLinkList.size() > 0) {
			for (Iterator iterator = identityLinkList.iterator(); iterator
					.hasNext();) {
				IdentityLink identityLink = (IdentityLink) iterator.next();
				if (identityLink.getUserId() != null) {
					users.add(identityLink.getUserId());
				}
				/*if (identityLink.getGroupId() != null) {
					// 根据组获得对应人员
					List userList = identityService.createUserQuery()
							.memberOfGroup(identityLink.getGroupId()).list();
					if (userList != null && userList.size() > 0)
						users.addAll(userList);
				}*/
			}
 
		}
		return StringUtils.join(users,",");
	}
	/**
	 * 
	 * <p>
	 * Title: getHistoryWorkList
	 * </p>
	 * 
	 * <p>
	 * Description:获取已办任务
	 * </p>
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param userName
	 * @return
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Page getHistoryWorkList(Pageable page, TaskQueryBo taskQueryBo) {
		TaskBo taskBo = null;
		List<TaskBo> taskList = new ArrayList<TaskBo>();
		List<HistoricTaskInstance> hisTaskInstanceList=null;
		// 设置查询信息
		HistoricTaskInstanceQuery historicTaskQuery = getHistoricTaskQuery(taskQueryBo);
		historicTaskQuery.orderByHistoricTaskInstanceEndTime().desc();
		// 获取数据
		if(page != null){
			int start = (page.getPageNow() - 1) * page.getPageSize();
			int limit = page.getPageSize();
			hisTaskInstanceList=historicTaskQuery.listPage(start, limit);
		}else{
			hisTaskInstanceList=historicTaskQuery.list();
		}
		if (hisTaskInstanceList != null && hisTaskInstanceList.size() > 0) {
			for (HistoricTaskInstance task : hisTaskInstanceList) {
				HistoricProcessInstance historicProcessInstance = (HistoricProcessInstance) historyService
						.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId())
						.singleResult();
				taskBo = new TaskBo();
				taskBo.setTaskId(task.getId());
				taskBo.setTaskName(task.getName());
				if (historicProcessInstance.getEndTime() != null) {
					taskBo.setStatus(ProcessStatus.FINISHED.getName());
				} else {
					taskBo.setStatus(ProcessStatus.RUNNING.getName());
				}
				taskBo.setApproveUserName(task.getAssignee());
				taskBo.setStartTime(Tools.formatDateToStr(0, task.getStartTime()));
				taskBo.setEndTime(Tools.formatDateToStr(0, task.getEndTime()));
				taskBo.setDueTime(Tools.formatDateToStr(0, task.getDueDate()));
				taskBo.setDuration(task.getDurationInMillis());
				taskBo.setProcessStartUserName(historicProcessInstance.getStartUserId());
				taskBo.setVersion(historicProcessInstance.getProcessDefinitionVersion());
				taskBo.setProcessDefineId(historicProcessInstance.getProcessDefinitionId());
				taskBo.setProcessInsId(historicProcessInstance.getId());
				taskBo.setProcessName(historicProcessInstance.getProcessDefinitionName());
				taskBo.setProcessStartTime(Tools.formatDateToStr(0, historicProcessInstance.getStartTime()));
				//业务数据ID
				if(historicProcessInstance.getBusinessKey() != null){
					taskBo.setBusinessId(historicProcessInstance.getBusinessKey());	
				}
				// 获取历史流程变量
				List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery()
						.processInstanceId(historicProcessInstance.getId()).list();
				if (varInstanceList != null && varInstanceList.size() > 0) {
					Map<String, Object> varMap = new HashMap<String, Object>();
					for (HistoricVariableInstance var : varInstanceList) {
						varMap.put(var.getVariableName(), var.getValue());
					}
					taskBo.setProcessVars(varMap);
				}
				/*//流程变量
				Map<String,Object> variables=taskService.getVariables(task.getId());
				taskBo.setProcessVars(variables);*/
				taskList.add(taskBo);
			}
		}
		if(page==null){
			return new Page(taskList);
		}else{
			return new Page(new Pageable(historicTaskQuery.count(), page.getPageNow()), taskList);
		}
	}
	/**
	 * 
	 * <p>
	 * Title: startProcess
	 * </p>
	 * <p>
	 * Description:启动流程
	 * </p>
	 * 
	 * @param processDefineId
	 *            流程定义ID
	 * @param processVars
	 *            表单数据
	 * 
	 */
	public ProcessInfo startProcess(String processDefineKey,String businessKey,String loginUserName,Map<String, Object> processVars) {
		log.info(String.format("启动流程开始,流程定义KEY:%s,流程业务ID:%s,登录用户ID:%s", processDefineKey,businessKey,loginUserName));
		log.info("提交流程参数:"+Tools.mapToStr(processVars));
		// 设置流程开启人员
		identityService.setAuthenticatedUserId(loginUserName);
		// 流程实例对象
		ProcessInstance currentProcess=null;
		// 开启流程，参数为 流程key,业务参数，业务id
		if(Tools.isNull(businessKey))
			currentProcess=runtimeService.startProcessInstanceByKey(processDefineKey,processVars);
		else
			currentProcess=runtimeService.startProcessInstanceByKey(processDefineKey,businessKey,processVars);
		ProcessInfo processInfo=new ProcessInfo();
		processInfo.setProInsId(currentProcess.getId());
		processInfo.setProDefId(currentProcess.getProcessDefinitionId());
		processInfo.setProDefKey(currentProcess.getProcessDefinitionKey());
		processInfo.setProcessName(currentProcess.getName());
		processInfo.setProDeploymentId(currentProcess.getDeploymentId());
		processInfo.setStartTime(Tools.formatDateToStr(0,currentProcess.getStartTime()));
		processInfo.setProStartUserId(currentProcess.getStartUserId());
		processInfo.setVersion(currentProcess.getProcessDefinitionVersion());
		log.info("启动流程结束");
		return processInfo;
	}

	/**
	 * 获取流程图像，已执行节点和流程线高亮显示
	 */
	public void getActivitiProccessImage(String pProcessInstanceId, HttpServletResponse response) {
		// logger.info("[开始]-获取流程图图像");
		try {
			// 获取历史流程实例
			HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
					.processInstanceId(pProcessInstanceId).singleResult();
			if (historicProcessInstance == null) {
				// throw new BusinessException("获取流程实例ID[" + pProcessInstanceId
				// + "]对应的历史流程实例失败！");
			} else {
				// 获取流程定义
				ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
						.getDeployedProcessDefinition(historicProcessInstance.getProcessDefinitionId());

				// 获取流程历史中已执行节点，并按照节点在流程中执行先后顺序排序
				List<HistoricActivityInstance> historicActivityInstanceList = historyService
						.createHistoricActivityInstanceQuery().processInstanceId(pProcessInstanceId)
						.orderByHistoricActivityInstanceId().asc().list();
				// 已执行的节点ID集合
				List<String> executedActivityIdList = new ArrayList<String>();
				//int index = 1;
				// logger.info("获取已经执行的节点ID");
				for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
					executedActivityIdList.add(activityInstance.getActivityId());
					// logger.info("第[" + index + "]个已执行节点=" +
					// activityInstance.getActivityId() + " : "
					// +activityInstance.getActivityName());
					//index++;
				}
				BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
				// 已执行的线集合
				List<String> flowIds = new ArrayList<String>();
				// 获取流程走过的线 (getHighLightedFlows是下面的方法)
				flowIds = getHighLightedFlows(bpmnModel, processDefinition, historicActivityInstanceList);
				// 获取流程图图像字符流
				ProcessDiagramGenerator pec = processEngine.getProcessEngineConfiguration()
						.getProcessDiagramGenerator();
				// 配置字体
				InputStream imageStream = pec.generateDiagram(bpmnModel, "png", executedActivityIdList, flowIds, "宋体",
						"微软雅黑", "黑体", null, 2.0);
				//response.setContentType("image/png");
				OutputStream os = response.getOutputStream();
				int bytesRead = 0;
				byte[] buffer = new byte[8192];
				while ((bytesRead = imageStream.read(buffer, 0, 8192)) != -1) {
					os.write(buffer, 0, bytesRead);
				}
				os.close();
				imageStream.close();
			}
			// logger.info("[完成]-获取流程图图像");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			// logger.error("【异常】-获取流程图失败！" + e.getMessage());
			// throw new BusinessException("获取流程图失败！" + e.getMessage());
		}
	}

	public List<String> getHighLightedFlows(BpmnModel bpmnModel, ProcessDefinitionEntity processDefinitionEntity,
			List<HistoricActivityInstance> historicActivityInstances) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 24小时制
		List<String> highFlows = new ArrayList<String>();// 用以保存高亮的线flowId
		for (int i = 0; i < historicActivityInstances.size() - 1; i++) {
			// 对历史流程节点进行遍历
			// 得到节点定义的详细信息
			FlowNode activityImpl = (FlowNode) bpmnModel.getMainProcess()
					.getFlowElement(historicActivityInstances.get(i).getActivityId());
			List<FlowNode> sameStartTimeNodes = new ArrayList<FlowNode>();// 用以保存后续开始时间相同的节点
			FlowNode sameActivityImpl1 = null;
			HistoricActivityInstance activityImpl_ = historicActivityInstances.get(i);// 第一个节点
			if(Tools.isNull(activityImpl_.getEndTime())){
				continue;
			}
			HistoricActivityInstance activityImp2_;
			for (int k = i + 1; k <= historicActivityInstances.size() - 1; k++) {
				activityImp2_ = historicActivityInstances.get(k);// 后续第1个节点
				if (activityImpl_.getActivityType().equals("userTask")
						&& activityImp2_.getActivityType().equals("userTask")
						&& df.format(activityImpl_.getStartTime()).equals(df.format(activityImp2_.getStartTime()))) // 都是usertask，且主节点与后续节点的开始时间相同，说明不是真实的后继节点
				{
				} else {
					sameActivityImpl1 = (FlowNode) bpmnModel.getMainProcess()
							.getFlowElement(historicActivityInstances.get(k).getActivityId());// 找到紧跟在后面的一个节点
					break;
				}
			}
			sameStartTimeNodes.add(sameActivityImpl1); // 将后面第一个节点放在时间相同节点的集合里
			for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
				HistoricActivityInstance activityImpl1 = historicActivityInstances.get(j);// 后续第一个节点
				HistoricActivityInstance activityImpl2 = historicActivityInstances.get(j + 1);// 后续第二个节点
				if (df.format(activityImpl1.getStartTime()).equals(df.format(activityImpl2.getStartTime()))) {// 如果第一个节点和第二个节点开始时间相同保存
					FlowNode sameActivityImpl2 = (FlowNode) bpmnModel.getMainProcess()
							.getFlowElement(activityImpl2.getActivityId());
					sameStartTimeNodes.add(sameActivityImpl2);
				} else {// 有不相同跳出循环
					break;
				}
			}
			List<SequenceFlow> pvmTransitions = activityImpl.getOutgoingFlows(); // 取出节点的所有出去的线
			for (SequenceFlow pvmTransition : pvmTransitions) {// 对所有的线进行遍历
				FlowNode pvmActivityImpl = (FlowNode) bpmnModel.getMainProcess()
						.getFlowElement(pvmTransition.getTargetRef());// 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
				if (sameStartTimeNodes.contains(pvmActivityImpl)) {
					highFlows.add(pvmTransition.getId());
				}
			}
		}
		return highFlows;
	}
	
	public void viewImage(String processDefinitionId, String processInstanceId, HttpServletResponse response)
			throws Exception {
		if (StringUtils.isEmpty(processInstanceId) == false) {
			getActivitiProccessImage(processInstanceId, response);
		} else {
			// 通过接口读取
			InputStream resourceAsStream = repositoryService.getProcessDiagram(processDefinitionId);
			//response.setContentType("image/png");
			// 输出资源内容到相应对象
			byte[] b = new byte[1024];
			int len = -1;
			while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
				response.getOutputStream().write(b, 0, len);
			}
		}
	}
	@Transactional
	public void completeTask(String taskId,String loginUserId, Map<String, Object> vars) throws Exception{
		//判断流程是否已结束
		org.activiti.engine.task.Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if(task==null){
			log.info(String.format("任务ID:%s不存在", taskId));
			throw new Exception("流程任务不存在");
		}
		String processInstanceId = task.getProcessInstanceId();
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()
				.processInstanceId(processInstanceId)//使用流程实例ID查询
				.singleResult();
		if(pi==null){
			log.info(String.format("流程ID:%s已结束", processInstanceId));
			throw new Exception("当前流程已结束");
		}
		//设置审批任务的执行人
	    taskService.claim(taskId,loginUserId);
		taskService.complete(taskId, vars);
	}
	/**
	 * 查询全部流程
	 * @param page
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Page findProcessDefinition(Pageable page) {
		//创建一个流程定义查询
		ProcessDefinitionQuery processDefinitionQuery=processEngine.getRepositoryService().createProcessDefinitionQuery();
		processDefinitionQuery.orderByProcessDefinitionVersion().asc();
		List<ProcessDefinition> processes=null;
		if(page != null){
			int start = (page.getPageNow() - 1) * page.getPageSize();
			int limit = page.getPageSize();
			processes=processDefinitionQuery.listPage(start, limit);
		}else{
			processes=processDefinitionQuery.list();
		}
		List<ProcessInfo> list=null;
		if(processes != null && processes.size()>0){
			list=new ArrayList<ProcessInfo>();
			ProcessInfo proInbfo=null;
			for(ProcessDefinition pro:processes){
				proInbfo=new ProcessInfo();
				proInbfo.setProDefId(pro.getId());
				proInbfo.setProDefKey(pro.getKey());
				proInbfo.setProcessName(pro.getName());
				proInbfo.setVersion(pro.getVersion());
				proInbfo.setProcessDesc(pro.getDescription());
				list.add(proInbfo);
			}
		}
		if(page==null){
			return new Page(list);
		}else{
			return new Page(new Pageable(processDefinitionQuery.count(), page.getPageNow()), list);
		}
	}
	public Page findMyProcesses(String loginUserName,Pageable page) {
		HistoricProcessInstanceQuery   processInstanceQuery =historyService.createHistoricProcessInstanceQuery(); 
		if(Tools.isNotNull(loginUserName)) {
			processInstanceQuery.startedBy(loginUserName);
		}
		List<HistoricProcessInstance> processes =null;
		if(page==null){
			processes = processInstanceQuery.list();
		}
		else{
			int start = (page.getPageNow() - 1) * page.getPageSize();
			int limit = page.getPageSize();
			processes = processInstanceQuery.listPage(start, limit);
		}
		List<ProcessInfo> list=null;
		if(processes != null && processes.size()>0){
			list=new ArrayList<ProcessInfo>();
			ProcessInfo proInbfo=null;
			for(HistoricProcessInstance pro:processes){
				proInbfo=new ProcessInfo();
				proInbfo.setProInsId(pro.getId());
				proInbfo.setProDefId(pro.getProcessDefinitionId());
				proInbfo.setProcessName(pro.getProcessDefinitionName());
				if(Tools.isNotNull(pro.getEndTime())){
					proInbfo.setStatus(ProcessStatus.FINISHED.getName());	
				}else
					proInbfo.setStatus(ProcessStatus.RUNNING.getName());
				proInbfo.setStartTime(Tools.formatDateToStr(0, pro.getStartTime()));
				proInbfo.setStartUserId(pro.getStartUserId());
				// 获取历史流程变量
				List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery()
						.processInstanceId(pro.getId()).list();
				if (varInstanceList != null && varInstanceList.size() > 0) {
					Map<String, Object> varMap = new HashMap<String, Object>();
					for (HistoricVariableInstance var : varInstanceList) {
						varMap.put(var.getVariableName(), var.getValue());
					}
					proInbfo.setVars(varMap);
				}
				list.add(proInbfo);
			}
		}
		if(page==null){
			return new Page(list);
		}
		return new Page(new Pageable(processInstanceQuery.count(), page.getPageNow()), list);
	}
	public String getImageStr(String processDefinitionId, String processInstanceId) throws Exception {
		byte[] data=new byte[0];
		if (StringUtils.isEmpty(processInstanceId) == false) {
				// 获取历史流程实例
				HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
						.processInstanceId(processInstanceId).singleResult();
				if (historicProcessInstance == null) {
				} else {
					// 获取流程定义
					ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
							.getDeployedProcessDefinition(historicProcessInstance.getProcessDefinitionId());
					// 获取流程历史中已执行节点，并按照节点在流程中执行先后顺序排序
					List<HistoricActivityInstance> historicActivityInstanceList = historyService
							.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
							.orderByHistoricActivityInstanceId().asc().list();
					// 已执行的节点ID集合
					List<String> executedActivityIdList = new ArrayList<String>();
					// logger.info("获取已经执行的节点ID");
					for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
						executedActivityIdList.add(activityInstance.getActivityId());
					}
					BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
					// 已执行的线集合
					List<String> flowIds = new ArrayList<String>();
					// 获取流程走过的线 (getHighLightedFlows是下面的方法)
					flowIds = this.getHighLightedFlows(bpmnModel, processDefinition, historicActivityInstanceList);
					// 获取流程图图像字符流
					ProcessDiagramGenerator pec = processEngine.getProcessEngineConfiguration()
							.getProcessDiagramGenerator();
					// 配置字体
					InputStream imageStream = pec.generateDiagram(bpmnModel, "png", executedActivityIdList, flowIds, "宋体",
							"微软雅黑", "黑体", null, 2.0);
					int bytesRead = 0;
					byte[] buffer = new byte[8192];
					while ((bytesRead = imageStream.read(buffer, 0, 8192)) != -1) {
						data=Tools.unitByteArray(data, Tools.subBytes(buffer, 0, bytesRead));
					}
					imageStream.close();
				}
		} else {
			// 通过接口读取
			InputStream resourceAsStream = repositoryService.getProcessDiagram(processDefinitionId);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = resourceAsStream.read(buffer, 0, 8192)) != -1) {
				data=Tools.unitByteArray(data, Tools.subBytes(buffer, 0, bytesRead));
			}
			resourceAsStream.close();
		}
		// 对字节数组Base64编码
		byte[] arr=Base64.getEncoder().encode(data);
		return new String(arr,"UTF-8");
	}

	@Transactional
	public void deleteProcessDefinition(String processDefinitionId) {
				ProcessDefinition processDefinition = repositoryService
						.createProcessDefinitionQuery()
						.processDefinitionId(processDefinitionId)
						.singleResult();
				//流程定义所属部署id
				String deploymentId = processDefinition.getDeploymentId();
			    //1.根据流程部署id删除这一次部署的所有流程定义
				//建议一次部署只部署一个流程，根据流程部署id删除一个流程的定义
				//约束：如果该流程定义没有启动流程实例可以删除，如果该流程定义以及启动流程实例，不允许删除，如果删除就抛出异常
				repositoryService.deleteDeployment(deploymentId);
	}

	public Page queryUserInvolvedProcesses(String userId, Pageable page, String processStatus) throws Exception {
		 //查询指定用户参与的流程信息 （流程历史  用户参与 ）
		HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery().involvedUser(userId);
		if(ProcessStatus.FINISHED.getCode().equals(processStatus)){
			historicProcessInstanceQuery.finished();
		}else if(ProcessStatus.RUNNING.getCode().equals(processStatus)){
			historicProcessInstanceQuery.unfinished();
		}else{
			
		}
		historicProcessInstanceQuery.orderByProcessInstanceStartTime().desc();
		List<HistoricProcessInstance> historicProcessInstanceList=null;
		if(page==null){
			historicProcessInstanceList = historicProcessInstanceQuery.list();
		}
		else{
			int start = (page.getPageNow() - 1) * page.getPageSize();
			int limit = page.getPageSize();
			historicProcessInstanceList = historicProcessInstanceQuery.listPage(start, limit);
		}
		List<ProcessInfo> list=null;
		if(historicProcessInstanceList != null && historicProcessInstanceList.size()>0){
			list=new ArrayList<ProcessInfo>();
			ProcessInfo proInbfo=null;
			for(HistoricProcessInstance pro:historicProcessInstanceList){
				proInbfo=new ProcessInfo();
				proInbfo.setProInsId(pro.getId());
				proInbfo.setProDefId(pro.getProcessDefinitionId());
				proInbfo.setProDefKey(pro.getProcessDefinitionKey());
				proInbfo.setProDeploymentId(pro.getDeploymentId());
				proInbfo.setProcessDesc(pro.getDescription());
				proInbfo.setProStartUserId(pro.getStartUserId());
				proInbfo.setDuration(Tools.isNotNull(pro.getDurationInMillis())?pro.getDurationInMillis():0);
				proInbfo.setProcessName(pro.getProcessDefinitionName());
				if(Tools.isNotNull(pro.getEndTime())){
					proInbfo.setEndTime(Tools.formatDateToStr(0,pro.getEndTime()));
					proInbfo.setStatus(ProcessStatus.FINISHED.getName());	
				}else{
					List<org.activiti.engine.task.Task> tasks = taskService.createTaskQuery().processInstanceId(pro.getId()).list();
					proInbfo.setCurrentTaskName(StringUtils.join(CollectionUtils.getIdsFromList(tasks,"name",String.class),","));
					proInbfo.setStatus(ProcessStatus.RUNNING.getName());
				}
					
				proInbfo.setStartTime(Tools.formatDateToStr(0, pro.getStartTime()));
				list.add(proInbfo);
			}
		}
		if(page==null){
			return new Page(list);
		}
		return new Page(new Pageable(historicProcessInstanceQuery.count(), page.getPageNow()), list);
	}

}
