package com.ctsi.activiti.demo;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.impl.DefaultProcessDiagramCanvas;

/**
 * Unit test for simple App.
 */
public class AppTest {

	static final Logger log = Logger.getLogger(AppTest.class.getSimpleName());

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	public static void main(String[] args) {
		
		AppTest t = new AppTest();
		
//		t.deploymentProcessDefinition();
		
//		t.startProcessInstance();
		
//		t.findMyPersonalTask();
		
//		t.completeMyPersonalTask();
		
//		t.generateImage();
		
		t.getAllNode();
		
	}
	
	public void getAllNode() {
		String key = "myProcess";
		RepositoryService repository = processEngine.getRepositoryService();
		ProcessDefinition pd = repository.createProcessDefinitionQuery().processDefinitionKey(key).latestVersion().singleResult();
		
		ProcessDefinitionEntity pde =(ProcessDefinitionEntity) ((RepositoryServiceImpl) repository).getDeployedProcessDefinition(pd.getId());
		
		log.info(pde.getDeploymentId() + ", " + pde.getResourceName());
		
		
		
		InputStream is = repository.getResourceAsStream(pde.getDeploymentId(), pde.getResourceName());
		XMLInputFactory xml = XMLInputFactory.newInstance();
		InputStreamReader isr = null;
		XMLStreamReader xsr = null;
		try {
			isr = new InputStreamReader(is);
			xsr = xml.createXMLStreamReader(isr);
			
			BpmnModel bm = new BpmnXMLConverter().convertToBpmnModel(xsr);
			Collection<FlowElement> collection = bm.getMainProcess().getFlowElements();
			for (FlowElement fe : collection) {
				log.info(fe.getId() + ", " + fe.getName());
			}
			
			Map<String, GraphicInfo> location = bm.getLocationMap();
			log.info("location " + location.size());
			Set<String> sLocation = location.keySet();
			for (String s : sLocation) {
				GraphicInfo g = location.get(s);
				log.info(s + ", " + g.getX() + ", " + g.getY() + ", " + g.getWidth() + ", " + g.getHeight());
			}
			
			Map<String, GraphicInfo> labelLocation = bm.getLabelLocationMap();
			log.info("labelLocation " + labelLocation.size());
			
			Map<String, List<GraphicInfo>> map = bm.getFlowLocationMap();
			Set<String> set = map.keySet();
			for (String s : set ) {
				List<GraphicInfo> list = map.get(s);
				for (GraphicInfo g : list) {
					log.info(s + ", " + g.getX() + ", " + g.getY());
				}
			}
			
			
//			DefaultProcessDiagramGenerator
			
			try {
				Class<?> clazz = Class
						.forName("org.activiti.image.impl.DefaultProcessDiagramGenerator");
				Method method = clazz.getDeclaredMethod(
						"initProcessDiagramCanvas", BpmnModel.class,
						String.class, String.class, String.class, String.class,
						ClassLoader.class);
				
				method.setAccessible(true);
				DefaultProcessDiagramCanvas dpdc = (DefaultProcessDiagramCanvas) method
						.invoke(clazz.newInstance(), bm, "png",
								"WenQuanYi Micro Hei", "WenQuanYi Micro Hei",
								"WenQuanYi Micro Hei", null);
				log.info(dpdc.toString());
				 
				clazz = Class
						.forName("org.activiti.image.impl.DefaultProcessDiagramCanvas");
				Field minXField = clazz.getDeclaredField("minX"); // 得到minX字段
				Field minYField = clazz.getDeclaredField("minY");
				minXField.setAccessible(true);
				minYField.setAccessible(true);
				int minX = minXField.getInt(dpdc);// 最小的x值
				int minY = minYField.getInt(dpdc); // 最小的y的值
				
				log.info("" + minX + ", " + minY);
			}
			catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			} 
			catch (NoSuchMethodException e) {
				e.printStackTrace();
			} 
			catch (SecurityException e) {
				e.printStackTrace();
			} 
			catch (IllegalAccessException e) {
				e.printStackTrace();
			} 
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			} 
			catch (InvocationTargetException e) {
				e.printStackTrace();
			} 
			catch (InstantiationException e) {
				e.printStackTrace();
			} 
			catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
			
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		finally {
			if (null != xsr) {
				try {
					xsr.close();
				} 
				catch (XMLStreamException e) {
					e.printStackTrace();
				}
			}
			if (null != is) {
				try {
					is.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	public void generateImage() {
		String key = "example";
		key = "myProcess";
		RepositoryService rs = processEngine.getRepositoryService();
		HistoryService hs = processEngine.getHistoryService();
		RuntimeService runtime = processEngine.getRuntimeService();
		
		ProcessDefinition pd = rs.createProcessDefinitionQuery().processDefinitionKey(key).latestVersion().singleResult();
		BpmnModel bm = rs.getBpmnModel(pd.getId());
		
		int id = 10001;
		id = 2501;
		List<HistoricActivityInstance> hai = hs.createHistoricActivityInstanceQuery().processInstanceId("" + id).orderByHistoricActivityInstanceId().asc().list();
		
		// 已执行的节点ID集合
        List<String> executedActivityIdList = new ArrayList<>();
        for (HistoricActivityInstance activityInstance : hai) {
            executedActivityIdList.add(activityInstance.getActivityId());
        }
        
        ProcessEngineConfiguration pec = processEngine.getProcessEngineConfiguration();
        log.info(pec.getActivityFontName() + ", " + pec.getAnnotationFontName() + ", " + pec.getLabelFontName());
		
		try (
//				InputStream is = processEngine.getProcessEngineConfiguration()
//								.getProcessDiagramGenerator().generateDiagram(bm, "png", executedActivityIdList);
				
				InputStream is = processEngine.getProcessEngineConfiguration()
								.getProcessDiagramGenerator().generateDiagram(bm, "png", Collections.<String>emptyList(), Collections.<String>emptyList(), "WenQuanYi Micro Hei", "WenQuanYi Micro Hei", "WenQuanYi Micro Hei", null, 1.0);
				
//				InputStream is = processEngine.getProcessEngineConfiguration()
//								.getProcessDiagramGenerator().generatePngDiagram(bm);
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("/home/lb/temp/image/a.png"));
		) {
			int size = 2048;
			byte[] buffer = new byte[size];
			int len = 0;
			while ( (len = is.read(buffer, 0, size)) > -1 ) {
				bos.write(buffer, 0, len);
			}
			bos.flush();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 部署流程定义 */
	public void deploymentProcessDefinition() {
		Deployment deployment = processEngine.getRepositoryService()// 与流程定义和部署对象相关的Service
				.createDeployment() // 创建一个部署对象
				.name("MyProcess入门程序")// 添加部署的名称
				.addClasspathResource("diagrams/MyProcess.bpmn")// 从classpath的资源中加载，一次只能加载一个文件
				.addClasspathResource("diagrams/MyProcess.png")// 从classpath的资源中加载，一次只能加载一个文件
				.deploy(); // 完成部署
		System.out.println("部署ID:" + deployment.getId()); // 1
		System.out.println("部署名称" + deployment.getName()); // helloworld入门程序

	}

	/** 启动流程实例 **/
	public void startProcessInstance() {
		// 流程定义的key
		String processDefinitionKey = "myProcess";
		ProcessInstance pi = processEngine.getRuntimeService()// 与正在执行
																// 的流程实例和执行对象相关的Service
				.startProcessInstanceByKey(processDefinitionKey); // 使用流程定义的key启动流程实例,key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动
		System.out.println("流程实例ID:" + pi.getId());
		System.out.println("流程定义ID:" + pi.getProcessDefinitionId());
	}

	/** 查询当前人的个人任务 */
	public void findMyPersonalTask() {
		String assignee = "张三";
		assignee = "李四";
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
		String taskId = "2505";
		processEngine.getTaskService()// 与正在执行的任务管理相关的Service
				.complete(taskId);
		System.out.println("完成任务：任务ID:" + taskId);
	}

}
