package com.huize.migrationcommon.service;

import com.huize.migrationcommon.entity.TableInfo;

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
}