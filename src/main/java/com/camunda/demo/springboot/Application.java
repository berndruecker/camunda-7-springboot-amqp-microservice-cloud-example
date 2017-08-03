package com.camunda.demo.springboot;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
@EnableProcessApplication
public class Application {

  public static void main(String... args) {
    SpringApplication.run(Application.class, args);

    // do default setup of platform (everything is only applied if not yet there)
    ProcessEngine engine = BpmPlatform.getDefaultProcessEngine();
    
//    // and add default users to Camunda to be ready-to-go
//    UserGenerator.createDefaultUsers(engine);
//
//    // Add User & Group for Callcenter, including proper filter
//    addUser(engine, "max", "max", "Max", "Mustermann");
//    addGroup(engine, "callcenter", "Call-Center", "max");
//    addFilterGroupAuthorization(engine, "callcenter", FILTER_MeineAufgaben, FILTER_GruppenAufgaben, FILTER_Ueberfaellig, FILTER_Wiedervorlage);
//
//    addFilterUserAuthorization(engine, "demo", FILTER_alleAufgaben);
//    
//    createGrantGroupAuthorization(engine, //
//        new String[]{"callcenter"}, //
//        new Permission[]{Permissions.READ, Permissions.READ_HISTORY, Permissions.UPDATE_INSTANCE}, //
//        Resources.PROCESS_DEFINITION, //
//        new String[] {ProcessConstants.PROCESS_KEY_registrierung}); 
//    
//    createGrantGroupAuthorization(engine, //
//        new String[]{"callcenter"}, //
//        new Permission[]{Permissions.READ}, //
//        Resources.DEPLOYMENT, //
//        new String[] {"*"});     
  }

}
