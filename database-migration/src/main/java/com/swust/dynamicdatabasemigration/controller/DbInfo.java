package com.swust.dynamicdatabasemigration.controller;

import java.util.List;
import java.util.Map;

/**
 * @author : MrLawrenc
 * date  2020/6/10 23:32
 * <p>
 * 定义一套规范
 */
public interface DbInfo {

    /**
     * 使用指定数据源查询表信息
     * <p>
     * 数据源的切换使用aop来设置，如下
     * <pre>
     *      DynamicDataSourceContextHolder.push("datasource");
     *      ...
     *      DynamicDataSourceContextHolder.clear();
     * </pre>
     *
     * @param datasource 数据源
     * @param table      表
     * @return 当前表的信息
     */
    List<Map<String, String>> selectTableInfo(String datasource, String table);
}