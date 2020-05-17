package com.example.dynamicdatasource.mappper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dynamicdatasource.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author : MrLawrenc
 * @date : 2020/5/16 0:36
 * @description : TODO
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

//    @Select("select * from information_schema.COLUMNS where TABLE_SCHEMA = (select database())")
    @Select("select * from information_schema.COLUMNS where TABLE_SCHEMA = (select database())  and TABLE_NAME=#{table} ")
    List<Map<String,String>> info(String table);
}