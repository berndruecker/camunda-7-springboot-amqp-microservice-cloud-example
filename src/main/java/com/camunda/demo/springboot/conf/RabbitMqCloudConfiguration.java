package com.camunda.demo.springboot.conf;


import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.common.AmqpServiceInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile("cloud")
public class RabbitMqCloudConfiguration {

  @Bean
  public ConnectionFactory rabbitConnectionFactory() {
    CloudFactory cloudFactory = new CloudFactory();
    Cloud cloud = cloudFactory.getCloud();
    AmqpServiceInfo serviceInfo = (AmqpServiceInfo) cloud.getServiceInfo("camunda-mq");
    String serviceID = serviceInfo.getId();
    ConnectionFactory connectionFactory = cloud.getServiceConnector(serviceID, ConnectionFactory.class, null);
//    System.out.println(connectionFactory);
    return connectionFactory;
//    String uri = System.getenv("CLOUDAMQP_URL");
//    if (uri == null) uri = "amqp://guest:guest@localhost";
//
//    ConnectionFactory factory = new ConnectionFactory();
//    factory.setUri(uri);
  }
}
