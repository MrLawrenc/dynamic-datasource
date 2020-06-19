package com.huize.migrationcommon.entity;

import lombok.Data;

import java.util.List;

/**
 * @author MrLawrenc
 * date  2020/6/14 14:03
 * <p>
 * 每一行数据
 */
@Data
public class Row {


    /**
     * 该行数据所对应的索引
     */
    private String index;

    /**
     * 每一行数据
     */
    private List<String> rowData;


    public enum Type {
        BAD, NULL, INT, LONG, DOUBLE, STRING, BOOL, DATE, BYTES
    }
}
