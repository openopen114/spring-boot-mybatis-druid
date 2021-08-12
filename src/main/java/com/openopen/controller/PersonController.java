package com.openopen.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.openopen.model.Person;
import com.openopen.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PersonController {

  // Logger
  private Logger logger = LoggerFactory.getLogger(PersonController.class);

  @Autowired private PersonService personService;

  /*
   *
   * 查詢 Person By ID
   *
   * */
  //http://localhost:8080/api/person/id/9
  @RequestMapping(
      value = "/person/id/{_id}",
      method = RequestMethod.GET,
      produces = {"application/json"})
  public Person getPersonById(@PathVariable("_id") String _id) {
    logger.info("getPersonById _id: " + _id);

    return personService.getPersonById(_id);
  }

  /*
   *
   * 新增 Person
   *
   * */
  //http://localhost:8080/api/add/person
  @PostMapping(
      value = "/add/person",
      produces = {"application/json"})
  @Transactional
  public String insertPersonByList(@RequestBody String _json) {
    logger.info("===> insertPersonByList");
    logger.info(_json);
    Gson gson = new Gson();
    List<Person> list = gson.fromJson(_json, new TypeToken<List<Person>>() {}.getType());

    personService.insertPersonByList(list);

    JsonObject obj = new JsonObject();
    obj.addProperty("ACTION", "insertPersonByList");
    obj.addProperty("RESULT", "OK");

    return new Gson().toJson(obj);
  }
}
