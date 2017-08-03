package com.camunda.demo.springboot.rest.dummy;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentDummyRestController {
  
  public static class CreateChargeRequest {
    public int amount;
  }

  @RequestMapping(path="/charges", method=RequestMethod.POST)
  public String createCharge(@RequestBody CreateChargeRequest request) {
    return "{\"transactionId\": \"77412\"}";
  }
}
