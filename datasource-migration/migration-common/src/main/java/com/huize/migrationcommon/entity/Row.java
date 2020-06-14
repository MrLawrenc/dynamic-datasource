package com.huize.migrationcommon.entity;

import java.util.List;

/**
 * @author MrLawrenc
 * date  2020/6/14 14:03
 * <p>
 * 每一列数据
 */
public class Row {

    /**
     * 每一行数据
     */
    private List<String> rowData;


    public Row(List<String> rowData) {
        this.rowData = rowData;
    }


    public enum Type {
        BAD, NULL, INT, LONG, DOUBLE, STRING, BOOL, DATE, BYTES
    }
}
