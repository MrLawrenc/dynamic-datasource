package com.huize.migrationcommon.entity;

import lombok.Data;

/**
 * @author hz20035009-逍遥
 * date   2020/6/22 16:28
 * <p>
 * <p>
 * 测试表对应的实体
 */
@Data
public class TestDatasource {
    private int id;
    private String name;
    private int companyId;
    private String type;
}