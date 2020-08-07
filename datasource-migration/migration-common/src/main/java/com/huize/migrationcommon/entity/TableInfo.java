package com.huize.migrationcommon.entity;

import lombok.Data;

/**
 * @author : MrLawrenc
 * date  2020/6/11 22:51
 * <p>
 * show columns from user;
 */
@Data
public class TableInfo {

    private String tableCatalog;

    /**
     * 列的数据类型
     */
    private String dataType;

    /**
     * 列名
     */
    private String columnName;

    /**
     * 列顺序
     */
    private int columnOrder;

    //todo


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableInfo tableInfo = (TableInfo) o;
        return columnOrder == tableInfo.columnOrder &&
                tableCatalog.equals(tableInfo.tableCatalog) &&
                dataType.equals(tableInfo.dataType) &&
                columnName.equals(tableInfo.columnName);
    }

}