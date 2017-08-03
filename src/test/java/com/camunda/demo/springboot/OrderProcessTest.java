package com.camunda.demo.springboot;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.run.ProcessRunner.ExecutableRunner.StartingByStarter;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.camunda.demo.springboot.adapter.AmqpReceiver;
import com.camunda.demo.springboot.conf.TestApplication;
import com.camunda.demo.springboot.rest.OrderRestController;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, //
    classes = TestApplication.class, //
    properties = { //
        "camunda.bpm.job-execution.enabled=false", //
        "camunda.bpm.auto-deployment-enabled=false", //
        "restProxyHost=api.example.org", //
        "restProxyPort=80" })
@Deployment(resources = { "order.bpmn" })
@ActiveProfiles({ "test" })
public class OrderProcessTest {

  @Mock
  private ProcessScenario orderProcess;
  
  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private RestTemplate restTemplate;

  private MockRestServiceServer mockRestServer;

  // Do not use the real one to avoid RabbitMQ being connected
  private AmqpReceiver amqpReceiver;
  
  @Autowired
  private OrderRestController orderRestController; 

  @Autowired
  private ProcessEngine processEngine;

  @Rule
  @ClassRule
  public static ProcessEngineRule rule;

  @PostConstruct
  void initRule() {
    rule =TestCoverageProcessEngineRuleBuilder.create(processEngine).build();
    // Without Coverage: new ProcessEngineRule(processEngine);
  }

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    
    mockRestServer = MockRestServiceServer.createServer(restTemplate);
    amqpReceiver = new AmqpReceiver(rule.getProcessEngine());

    // default behavior for ReceiveTask's in process: just continue
    when(orderProcess.waitsAtReceiveTask(anyString())).thenReturn((messageSubscription) -> messageSubscription.receive());
  }

  @Test
  public void testOrderHappyPath() throws Exception {
    String orderId = UUID.randomUUID().toString();

    StartingByStarter starter = Scenario.run(orderProcess) //
      .startBy(() -> {
        // use the real thing which would also start the flow in the real system
        // as this might do other things, foremost data transformation and naming of process variables
        return orderRestController.placeOrder(orderId, 547);
      });
    
    // expect the charge for retrieving payments to be created correctly and return a dummy transactionId
    mockRestServer
        .expect(requestTo("http://api.example.org:80/payment/charges")) //
        .andExpect(method(HttpMethod.POST))
        .andExpect(jsonPath("amount").value("547"))
        .andRespond(withSuccess("{\"transactionId\": \"12345\"}", MediaType.APPLICATION_JSON));
    
    when(orderProcess.waitsAtReceiveTask("ReceiveTask_WaitForGoodsShipped")).thenReturn((messageSubscription) -> {
      assertEquals(ProcessConstants.MSG_NAME_GoodsShipped, messageSubscription.getEventName());
      // again: use the real thing like receiving the real AMQP message, return a dummy shipmentId
      amqpReceiver.handleGoodsShippedEvent(orderId, "0815");
    });    

    when(orderProcess.waitsAtTimerIntermediateEvent(anyString())).thenReturn((processInstance) -> {
      processInstance.defer("PT10M", () -> {fail("Timer should have fired in the meanwhile");}); 
    });
    
    // OK - everything prepared - let's go
    Scenario scenario = starter.execute();
    
    mockRestServer.verify();

    // and very that some things happened
    assertThat(scenario.instance(orderProcess)).variables().containsEntry(ProcessConstants.VARIABLE_paymentTransactionId, "12345");
    assertThat(scenario.instance(orderProcess)).variables().containsEntry(ProcessConstants.VAR_NAME_shipmentId, "0815");

    {
      ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
      verify(rabbitTemplate, times(1)).convertAndSend(eq("shipping"), eq("createShipment"), argument.capture());
      // if the body would be an object, JSON or whatever, you can easily inspect/assert it here in detail
      assertEquals(orderId, argument.getValue());
    }

    verify(orderProcess).hasFinished("EndEvent_OrderShipped");
  }
}
