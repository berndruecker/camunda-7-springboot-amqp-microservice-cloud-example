package com.camunda.demo.springboot.adapter;

import java.util.UUID;

import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.camunda.demo.springboot.ProcessConstants;

@Component
@Profile("!test")
public class AmqpReceiver {

  @Autowired
  private ProcessEngine camunda;

  public AmqpReceiver() {
  }
  
  public AmqpReceiver(ProcessEngine camunda) {
    this.camunda = camunda;
  }
  
  /**
   * Dummy method to handle the shipGoods command message - as we do not have a 
   * shipping system available in this small example
   */
  @RabbitListener(bindings = @QueueBinding( //
      value = @Queue(value = "shipping_create_test", durable = "true"), //
      exchange = @Exchange(value = "shipping", type = "topic", durable = "true"), //
      key = "*"))
  @Transactional  
  public void dummyShipGoodsCommand(String orderId) {
    // and call back directly with a generated transactionId
    handleGoodsShippedEvent(orderId, UUID.randomUUID().toString());
  }

  public void handleGoodsShippedEvent(String orderId, String shipmentId) {
    camunda.getRuntimeService().createMessageCorrelation(ProcessConstants.MSG_NAME_GoodsShipped) //
        .processInstanceVariableEquals(ProcessConstants.VAR_NAME_orderId, orderId) //
        .setVariable(ProcessConstants.VAR_NAME_shipmentId, shipmentId) //
        .correlateWithResult();
  }
  
}
