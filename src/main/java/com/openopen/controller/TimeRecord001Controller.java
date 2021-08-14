package com.openopen.controller;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.openopen.dao.PostgresMapper;
import com.openopen.dao.TIMERECORD001Mapper;
import com.openopen.model.Person;
import com.openopen.model.TIMERECORD001;
import com.openopen.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TimeRecord001Controller {
    // Logger
    private Logger logger = LoggerFactory.getLogger(TimeRecord001Controller.class);



    @Autowired
    private PostgresMapper postgresMapper;



    @Autowired
    private TIMERECORD001Mapper timeRecord001Mapper;



    /*
     *
     * 查詢 Person By ID
     *
     * */
    //http://localhost:8080/api/tr001/id/1
    @RequestMapping(
            value = "/tr001/id/{_id}",
            method = RequestMethod.GET,
            produces = {"application/json"})
    public TIMERECORD001 getTr001ById(@PathVariable("_id") Integer _id) {
        logger.info("getTr001ById _id: " + _id);

        return timeRecord001Mapper.selectByPrimaryKey(_id);
    }



    /*
     *
     * 新增 Person
     *
     * */
    //http://localhost:8080/api/add/tr001
    @PostMapping(
            value = "/add/tr001",
            produces = {"application/json"})
    @Transactional
    public String insertTr001ByList(@RequestBody String _json) {
        logger.info("===> insertTr001ByList");
        logger.info(_json);
        Gson gson = new Gson();
        List<TIMERECORD001> list = gson.fromJson(_json, new TypeToken<List<TIMERECORD001>>() {}.getType());

        postgresMapper.setTimezone();


        for(TIMERECORD001 model:list){
            timeRecord001Mapper.insertSelective(model);
        }



        JsonObject obj = new JsonObject();
        obj.addProperty("ACTION", "insertTr001ByList");
        obj.addProperty("RESULT", "OK");

        return new Gson().toJson(obj);
    }



}
