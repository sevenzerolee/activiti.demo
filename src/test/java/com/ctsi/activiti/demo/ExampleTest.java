package com.ctsi.activiti.demo;

import java.util.List;
import java.util.logging.Logger;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

/**
 * Unit test for simple App.
 */
public class ExampleTest {

	static final Logger log = Logger.getLogger(ExampleTest.class.getSimpleName());

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	public static void main(String[] args) {
		
		ExampleTest t = new ExampleTest();
		
//		t.deploymentProcessDefinition();
		
//		t.startProcessInstance();
		
//		t.findMyPersonalTask();
		
		t.completeMyPersonalTask();
		
	}

	public void before() {
	}

	/** 部署流程定义 */
	public void deploymentProcessDefinition() {
		Deployment deployment = processEngine.getRepositoryService()// 与流程定义和部署对象相关的Service
				.createDeployment() // 创建一个部署对象
				.name("example程序")// 添加部署的名称
				.addClasspathResource("diagrams/example.bpmn")// 从classpath的资源中加载，一次只能加载一个文件
				.addClasspathResource("diagrams/example.png")// 从classpath的资源中加载，一次只能加载一个文件
				.deploy(); // 完成部署
		System.out.println("部署ID:" + deployment.getId()); // 1
		System.out.println("部署名称" + deployment.getName()); // helloworld入门程序

	}

	/** 启动流程实例 **/
	public void startProcessInstance() {
		// 流程定义的key
		String processDefinitionKey = "example";
		ProcessInstance pi = processEngine.getRuntimeService()// 与正在执行
																// 的流程实例和执行对象相关的Service
				.startProcessInstanceByKey(processDefinitionKey); // 使用流程定义的key启动流程实例,key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动
		System.out.println("流程实例ID:" + pi.getId());
		System.out.println("流程定义ID:" + pi.getProcessDefinitionId());
	}

	/** 查询当前人的个人任务 */
	public void findMyPersonalTask() {
		String assignee = "one";
//		assignee = "李四";
		List<Task> list = processEngine.getTaskService()// 与正在执行的任务管理相关的Service
				.createTaskQuery()// 创建任务查询
				.taskAssignee(assignee)// 指定个人任查询，指定办理人
				.list();
		if (list != null && list.size() > 0) {
			for (Task task : list) {
				System.out.println("任务ID:" + task.getId());
				System.out.println("任务名称:" + task.getName());
				System.out.println("任务的创建时间:" + task.getCreateTime());
				System.out.println("任务的办理人:" + task.getAssignee());
				System.out.println("流程实例ID:" + task.getProcessInstanceId());
				System.out.println("执行对象ID:" + task.getExecutionId());
				System.out.println("流程定义ID:" + task.getProcessDefinitionId());
				System.out
						.println("############################################");
			}
		}
	}

	/** 完成我的任务 */
	public void completeMyPersonalTask() {
		// 任务ID
		String taskId = "10005";
		processEngine.getTaskService()// 与正在执行的任务管理相关的Service
				.complete(taskId);
		System.out.println("完成任务：任务ID:" + taskId);
	}

}
