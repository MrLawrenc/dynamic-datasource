package com.huize.migrationcommon;

import com.huize.migrationcommon.entity.TableInfo;

import java.util.List;

/**
 * @author hz20035009-逍遥
 * date   2020/6/22 16:52
 */
public interface WriterReader {

    /**
     * 表结构信息
     */
    List<TableInfo> tableConstruct(String tableName);
}
