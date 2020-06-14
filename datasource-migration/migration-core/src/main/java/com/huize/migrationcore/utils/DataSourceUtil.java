package com.huize.migrationcore.utils;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceCreator;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.huize.migrationcommon.entity.DataSourceInfo;
import org.springframework.beans.BeanUtils;

import javax.sql.DataSource;
import java.util.Set;

/**
 * @author : MrLawrenc
 * date  2020/6/13 23:38
 */
public class DataSourceUtil {

    /**
     * 向当前环境添加新数据源
     *
     * @param dataSourceInfo    数据源信息
     * @param dataSourceCreator creator
     * @param dataSourceRouting routing
     * @return 当前数据源所有key
     */
    public static Set<String> addDataSource(DataSourceInfo dataSourceInfo, DataSourceCreator dataSourceCreator, DataSource dataSourceRouting) {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        BeanUtils.copyProperties(dataSourceInfo, dataSourceProperty);
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSourceRouting;
        DataSource dataSource = dataSourceCreator.createDataSource(dataSourceProperty);
        ds.addDataSource(dataSourceInfo.getName(), dataSource);
        return ds.getCurrentDataSources().keySet();
    }
}