package com.openopen;

import com.openopen.model.Person;
import com.openopen.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@ComponentScan(basePackages = {"com.openopen"})
@RestController
public class App {


  @Value("${APIENV}")
  private   String APIENV;



  @RequestMapping(value = "/")
  String hello() {

    return "Hello World!" + APIENV;
  }

  @RequestMapping(value = "/env")
  String getApiEnv() {
    return APIENV;
  }

//  @RequestMapping(
//      value = "/person",
//      method = RequestMethod.GET,
//      produces = {"application/json"})
//  public Person getPerson() {
//    return personService.getPerson();
//  }

  public static void main(String[] args) {

    SpringApplication.run(App.class, args);
  }
}
