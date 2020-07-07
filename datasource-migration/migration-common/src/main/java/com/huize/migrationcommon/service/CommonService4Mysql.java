package com.huize.migrationcommon.service;

import com.huize.migrationcommon.entity.TableInfo;

import java.util.Collection;
import java.util.List;

/**
 * @author hz20035009-逍遥
 * date   2020/7/3 15:48
 */
public interface CommonService4Mysql {

    /**
     * @param table 表名
     * @return 根据列排好序的列信息
     */
    List<TableInfo> tableInfoList(String table);

    boolean save(String table, List<Collection<Object>> values);

    boolean update(String table, String primaryKey,
                   List<Object> primaryValues,
                   List<String> columnNames,
                   List<Collection<Object>> columnValues);
}