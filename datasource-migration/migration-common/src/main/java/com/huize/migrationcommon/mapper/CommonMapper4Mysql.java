package com.huize.migrationcommon.mapper;

import com.huize.migrationcommon.entity.TableInfo;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.session.ResultHandler;

import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hz20035009-逍遥
 * date   2020/6/11 15:40
 * <p>
 * mysql通用查询
 */
@Mapper
public interface CommonMapper4Mysql {


    /**
     * 获取当前数据源指定的表结构信息
     *
     * @param table 目标表
     * @return 表结构
     */
    @Select("select * from information_schema.COLUMNS where TABLE_SCHEMA = (select database())  and TABLE_NAME=#{table} ")
    @Results({
            @Result(property = "tableCatalog", column = "TABLE_CATALOG"),
            @Result(property = "dataType", column = "DATA_TYPE"),
            @Result(property = "columnName", column = "COLUMN_NAME"),
            @Result(property = "columnOrder",column = "ORDINAL_POSITION")
    })
    List<TableInfo> tableInfoList(String table);


    /**
     * 获取数据库相关信息
     */
    @Select("select * from information_schema.COLUMNS where TABLE_SCHEMA = (select database())")
    List<Map<String, String>> databaseInfo();

    /**
     * 获取数据库相关信息
     *
     * 表名不能使用预编译（#取值），会报错，替换为$取值
     */
/*    @Insert("<script>" +
            "insert into #{table} values " +
            "<foreach item=\"item\" index=\"index\" collection=\"values\" open=\"(\" separator=\",\" close=\")\">  \n" +

          "  <foreach item=\"item1\" index=\"index1\" collection=\"#{item}\" open=\"(\" separator=\",\" close=\")\">   #{item1} </foreach>"+

            "　　</foreach> "+

            "</script>")*/
   void save(@Param("table") String table,@Param("values") List<Collection<Object>> values);

    boolean update(@Param("table")String table, @Param("primaryKey")String primaryKey,
                   @Param("primaryValues")List<Object> primaryValues,
                   @Param("columnNames")List<String> columnNames,
                   @Param("rows")List<Collection<Object>> rows);
    /**
     * 通用条件查询
     * 非预编译的流式查询，后续更改为全响应式操作 r2dbc
     *
     * 结果集为map的情况会过滤掉为null的列的值
     * @param table     表
     * @param condition where 条件子句
     * @param handler   数据集，结果回调
     *
     *                  返回类型使用LinkedHashMap可以保证和列顺序一致
     */
    @Select("select * from ${table} where ${condition} ")
    @Options(statementType = StatementType.STATEMENT, resultSetType = ResultSetType.FORWARD_ONLY, fetchSize = Integer.MIN_VALUE)
    @ResultType(LinkedHashMap.class)
    void streamsSelect(@Param("table") String table, @Param("condition") @NotEmpty String condition, ResultHandler<Map<String, Object>> handler);


    //只能查列
    @Select("select * from ${table} where ${condition} ")
    @Options(statementType = StatementType.STATEMENT, resultSetType = ResultSetType.FORWARD_ONLY, fetchSize = Integer.MIN_VALUE)
    @ResultType(String.class)
    void streamsSelect1(@Param("table") String table, @Param("condition") @NotEmpty String condition, ResultHandler<String> handler);


    /**
     * 游标 流式查询
     * <p>
     * 测试用
     * <a herf=" https://my.oschina.net/yidinghe/blog/3288508"></a>
     */
    @Deprecated
    @Options(resultSetType = ResultSetType.FORWARD_ONLY)
    @Select("SELECT * FROM data_source_config ")
    Cursor<Map<String, String>> cursorQueryDepartmentAll();
}