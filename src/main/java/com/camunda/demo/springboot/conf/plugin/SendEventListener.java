package com.camunda.demo.springboot.conf.plugin;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

/**
 * In this listener you can do basically everything. You could also implement it as 
 * Spring bean an e.g. send events via Kafka to a central monitor - or to call the ELK stack
 * to hand over some events.
 */
public class SendEventListener implements ExecutionListener {

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		System.out.println("Hello world. There was an event '" + execution.getEventName() + "'! It came from activity '"+execution.getCurrentActivityId()+"' for process instance '" + execution.getProcessInstanceId() + "'");
	}

}
