package com.camunda.demo.springboot.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
public class HostsAndPorts {
 
  private String rabbitmqHost;
  private int rabbitmqPort;

  public HostsAndPorts() {
  }

  public String getRabbitmqHost() {
    return rabbitmqHost;
  }

  public void setRabbitmqHost(String rabbitmqHost) {
    this.rabbitmqHost = rabbitmqHost;
  }

  public int getRabbitmqPort() {
    return rabbitmqPort;
  }

  public void setRabbitmqPort(int rabbitmqPort) {
    this.rabbitmqPort = rabbitmqPort;
  }
}