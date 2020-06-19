package com.huize.migrationcommon.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huize.migrationcommon.entity.TableInfo;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.ResultHandler;

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
     * 流式查询，后续更改为全响应式操作 r2dbc
     *
     * @param table     表
     * @param condition where 条件子句
     * @return 查询结果
     */
    @Options(resultSetType = ResultSetType.FORWARD_ONLY, fetchSize = Integer.MIN_VALUE)
    @Select("select * from #{table} where #{condition} ")
    List<Map<String, String>> where(String table, String condition);

    /**
     * 流式查询
     */
    @Select("select * from user")
    @Options(resultSetType = ResultSetType.FORWARD_ONLY, fetchSize = 1000)
    @ResultType(Map.class)
    void getNeedSignOffUserCheckRecord(ResultHandler<Map<String, String>> handler);

    @Select("select * from user")
    @Options(resultSetType = ResultSetType.FORWARD_ONLY, fetchSize = 1000)
    @ResultType(Map.class)
    void getNeedSignOffUserCheckRecord(QueryWrapper wrapper, ResultHandler<Map<String, String>> handler);


    /**
     * 游标
     *
     * mapper方法执行完毕后连接就会关闭，因此会报错:java.lang.IllegalStateException: A Cursor is already closed.
     *
     * 解决:https://my.oschina.net/yidinghe/blog/3288508
     */
    @Options(resultSetType = ResultSetType.FORWARD_ONLY)
    @Select("SELECT * FROM user ")
    Cursor<Map<String, String>> cursorQueryDepartmentAll();
}