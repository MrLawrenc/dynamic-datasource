package com.huize.migrationcore.utils;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceCreator;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.huize.migrationcommon.entity.DataSourceInfo;
import org.springframework.beans.BeanUtils;

import javax.sql.DataSource;

/**
 * @author : MrLawrenc
 * date  2020/6/13 23:38
 */
public final class DataSourceUtil {

    /**
     * 向当前环境添加新数据源
     *
     * @param dataSourceInfo    数据源信息
     * @param dataSourceCreator creator
     * @param dataSourceRouting routing
     * @return 当前数据源所有key
     */
    public static DataSource addDataSource(DataSourceInfo dataSourceInfo, DataSourceCreator dataSourceCreator, DynamicRoutingDataSource ds) {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        BeanUtils.copyProperties(dataSourceInfo, dataSourceProperty);
        DataSource dataSource = dataSourceCreator.createDataSource(dataSourceProperty);
        ds.addDataSource(dataSourceInfo.getName(), dataSource);
        return dataSource;
    }
}