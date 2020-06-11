package com.swust.dynamicdatabasemigration.controller.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swust.dynamicdatabasemigration.controller.TableInfo;
import com.swust.dynamicdatabasemigration.controller.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author : MrLawrenc
 * @date : 2020/5/16 0:36
 * @description : TODO
 */
@Mapper
public interface MyMapper extends BaseMapper<User> {

//    @Select("select * from information_schema.COLUMNS where TABLE_SCHEMA = (select database())")
    @Select("select * from information_schema.COLUMNS where TABLE_SCHEMA = (select database())  and TABLE_NAME=#{table} ")
    List<Map<String,String>> info(String table);

    @Select("select * from information_schema.COLUMNS where TABLE_SCHEMA = (select database())  and TABLE_NAME=#{table} ")
    @Results({
            @Result(property = "tableCatalog", column = "TABLE_CATALOG"),
            @Result(property = "dataType", column = "DATA_TYPE")
    })
    List<TableInfo> info0(String table);

    @Select("select * from information_schema.COLUMNS where TABLE_SCHEMA = (select database())")
    List<Map<String,String>> infoAll();

    /**
     * 通用条件查询
     * @param table 表
     * @param condition where 条件子句
     * @return 查询结果
     */
    @Select("select * from #{table} where #{condition} ")
    List<Map<String,String>> where(String table,String condition);

}