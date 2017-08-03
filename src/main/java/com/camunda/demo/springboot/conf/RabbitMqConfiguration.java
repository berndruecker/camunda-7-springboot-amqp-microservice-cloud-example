package com.camunda.demo.springboot.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class RabbitMqConfiguration {
  
  public static final String QUEUE_NAME_test = "test-queue";
  public static final String EXCHANGE_NAME_test = "test-exchange";

  public static final String QUEUE_NAME_camunda = "camunda-queue";
  public static final String EXCHANGE_NAME_camunda = "camunda-exchange";
  
  public static final String PROCESS_VARIABLE_CORRELATION_ID = "amqp-correlation-id";

//  @Autowired
//  private HostsAndPorts hostsAndPorts;

//  @Bean
//  public MessageConverter messageConverter() {
//    return new Jackson2JsonMessageConverter();
//  }
//  
//  @Bean
//  protected RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//    RabbitTemplate rabbitTemplate = new RabbitTemplate();
//    rabbitTemplate.setMessageConverter(messageConverter());
//    rabbitTemplate.setConnectionFactory(connectionFactory);
//    return rabbitTemplate;
//  }
  
//  // only required if non standard ports are used
  // should be reconfigured by CloudFoundry
//  @Bean
//  public ConnectionFactory connectionFactory() {
//      return new CachingConnectionFactory(hostsAndPorts.getRabbitmqHost(), hostsAndPorts.getRabbitmqPort());
//  }  
 
}
