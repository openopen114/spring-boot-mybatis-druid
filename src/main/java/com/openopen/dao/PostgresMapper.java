package com.openopen.dao;


import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostgresMapper {
    // 設定時區  Asia/Taipei
    void setTimezone();

}
