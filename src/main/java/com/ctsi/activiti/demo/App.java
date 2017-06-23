package com.ctsi.activiti.demo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;

/**
 * Hello world!
 *
 */
public class App {
	
	public static void main(String[] args) {
		System.out.println("Hello World!");
		
//		ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault().setDatabaseSchemaUpdate(ProcessEngineConfigurationImpl.DB_SCHEMA_UPDATE_CREATE).buildProcessEngine();
		
		ProcessEngine processEngine = ProcessEngineConfiguration  
                .createProcessEngineConfigurationFromResource(  
                        "activiti.cfg.xml").buildProcessEngine();  
        System.out.println("processEngine:" + processEngine);  
		
	}
	
}
