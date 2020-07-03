package com.huize.migrationcommon.entity;

import lombok.Data;

import java.util.List;

/**
 * @author hz20035009-逍遥
 * date   2020/7/2 10:57
 * <p>
 * 表结构
 */
@Data
public class TableConstruct {
    private String tableName;

    private List<String> columnName;
    private List<Object> columnType;
}