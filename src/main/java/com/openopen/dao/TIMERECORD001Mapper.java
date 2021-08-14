package com.openopen.dao;

import com.openopen.model.TIMERECORD001;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface TIMERECORD001Mapper {
    int deleteByPrimaryKey(Integer serialNo);

    int insert(TIMERECORD001 record);

    int insertSelective(TIMERECORD001 record);

    TIMERECORD001 selectByPrimaryKey(Integer serialNo);

    int updateByPrimaryKeySelective(TIMERECORD001 record);

    int updateByPrimaryKey(TIMERECORD001 record);
}