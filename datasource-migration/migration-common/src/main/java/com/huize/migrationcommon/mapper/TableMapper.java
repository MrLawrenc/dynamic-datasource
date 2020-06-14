package com.huize.migrationcommon.mapper;

import com.huize.migrationcommon.entity.TableInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author hz20035009-逍遥
 * date   2020/6/11 15:40
 * <p>
 * 获取表相关信息
 */
@Mapper
public interface TableMapper {


    /**
     * 获取当前数据源指定的表结构信息
     *
     * @param table 目标表
     * @return 表结构
     */
    @Select("select * from information_schema.COLUMNS where TABLE_SCHEMA = (select database())  and TABLE_NAME=#{table} ")
    @Results({
            @Result(property = "tableCatalog", column = "TABLE_CATALOG"),
            @Result(property = "dataType", column = "DATA_TYPE")
    })
    List<TableInfo> tableInfoList(String table);


    @Select("select * from information_schema.COLUMNS where TABLE_SCHEMA = (select database())  and TABLE_NAME=#{table} ")
    List<Map<String, String>> info(String table);


    @Select("select * from information_schema.COLUMNS where TABLE_SCHEMA = (select database())")
    List<Map<String, String>> infoAll();

    /**
     * 通用条件查询
     *
     * @param table     表
     * @param condition where 条件子句
     * @return 查询结果
     */
    @Select("select * from #{table} where #{condition} ")
    List<Map<String, String>> where(String table, String condition);

}