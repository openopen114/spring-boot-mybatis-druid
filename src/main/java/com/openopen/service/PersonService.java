package com.openopen.service;

import com.openopen.dao.PersonMapper;
import com.openopen.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {


    @Autowired
    private PersonMapper personMapper;



    public Person getPerson(){
        System.out.println("===> get person");
        return personMapper.selectByPrimaryKey("1");

    }


}
