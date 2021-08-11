package com.openopen;

import com.openopen.model.Person;
import com.openopen.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@ComponentScan(basePackages={"com.openopen"})
@RestController
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }



    @Autowired
    private  PersonService personService;

    @RequestMapping(value = "/")
    String hello() {
        return "Hello World!";
    }


    @RequestMapping(value = "/person", method= RequestMethod.GET ,produces = { "application/json" })
    public Person getPerson(){
        return  personService.getPerson();
    }
}