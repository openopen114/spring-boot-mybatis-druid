package com.openopen.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.openopen.config.DruidConfig;
import com.openopen.dao.PersonMapper;
import com.openopen.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {


    // Logger
    private Logger logger = LoggerFactory.getLogger(PersonService.class);

    // Autowired Mapper
    @Autowired
    private PersonMapper personMapper;

    /*
     *
     * 查詢 Person By ID
     *
     * */
    public Person getPersonById(String _id) {
        logger.info("==> getPersonById ");
        return personMapper.selectByPrimaryKey(_id);
    }

    /*
     *
     * 新增 Person
     *
     * */
    public void insertPersonByList(List<Person> _list) {
        logger.info("==> insertPersonByList ");

        // 設定時區  Asia/Taipei
        personMapper.setTimezone();

        for (Person _model : _list) {
            logger.info("===> " + _model.getId());
            personMapper.insertSelectiveWithTimestamp(_model);
        }
    }
}
