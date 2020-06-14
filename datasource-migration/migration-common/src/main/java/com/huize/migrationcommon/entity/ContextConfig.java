package com.huize.migrationcommon.entity;

import lombok.Data;

import java.util.Map;

/**
 * @author : MrLawrenc
 * date  2020/6/12 23:25
 */
@Data
public class ContextConfig {

    private Map<String, DataSourceInfo> dataSourceMap;
}