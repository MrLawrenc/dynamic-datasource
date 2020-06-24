package com.huize.migrationcore.controller.config;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.*;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.huize.migrationcommon.entity.DataSourceInfo;
import com.huize.migrationcommon.mapper.CommonMapper4Mysql;
import com.huize.migrationreader.service.TableInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


/**
 * @author hz20035009-逍遥
 * date   2020/6/11 18:08
 */
@RestController
@AllArgsConstructor
@RequestMapping("/datasources")
@Api(tags = "数据源相关配置")
public class DataSourceConfigController {

    private final javax.sql.DataSource dataSource;
    private final DataSourceCreator dataSourceCreator;
    private final BasicDataSourceCreator basicDataSourceCreator;
    private final JndiDataSourceCreator jndiDataSourceCreator;
    private final DruidDataSourceCreator druidDataSourceCreator;
    private final HikariDataSourceCreator hikariDataSourceCreator;


    @GetMapping
    @ApiOperation("获取当前所有数据源")
    public Set<String> now() {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        return ds.getCurrentDataSources().keySet();
    }

    @PostMapping("/add")
    @ApiOperation("通用添加数据源（推荐）")
    public Set<String> add(@Validated @RequestBody DataSourceInfo dto) {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        BeanUtils.copyProperties(dto, dataSourceProperty);
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        javax.sql.DataSource dataSource = dataSourceCreator.createDataSource(dataSourceProperty);
        ds.addDataSource(dto.getPoolName(), dataSource);
        return ds.getCurrentDataSources().keySet();
    }


    @PostMapping("/addBasic")
    @ApiOperation(value = "添加基础数据源", notes = "调用Springboot内置方法创建数据源，兼容1,2")
    public Set<String> addBasic(@Validated @RequestBody DataSourceInfo dto) {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        BeanUtils.copyProperties(dto, dataSourceProperty);
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        javax.sql.DataSource dataSource = basicDataSourceCreator.createDataSource(dataSourceProperty);
        ds.addDataSource(dto.getPoolName(), dataSource);
        return ds.getCurrentDataSources().keySet();
    }

    @PostMapping("/addJndi")
    @ApiOperation("添加JNDI数据源")
    public Set<String> addJndi(String pollName, String jndiName) {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        javax.sql.DataSource dataSource = jndiDataSourceCreator.createDataSource(jndiName);
        ds.addDataSource(pollName, dataSource);
        return ds.getCurrentDataSources().keySet();
    }

    @PostMapping("/addDruid")
    @ApiOperation("基础Druid数据源")
    public Set<String> addDruid(@Validated @RequestBody DataSourceInfo dto) {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        BeanUtils.copyProperties(dto, dataSourceProperty);
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        javax.sql.DataSource dataSource = druidDataSourceCreator.createDataSource(dataSourceProperty);
        ds.addDataSource(dto.getPoolName(), dataSource);
        return ds.getCurrentDataSources().keySet();
    }

    @PostMapping("/addHikariCP")
    @ApiOperation("基础HikariCP数据源")
    public Set<String> addHikariCP(@Validated @RequestBody DataSourceInfo dto) {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        BeanUtils.copyProperties(dto, dataSourceProperty);
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        javax.sql.DataSource dataSource = hikariDataSourceCreator.createDataSource(dataSourceProperty);
        ds.addDataSource(dto.getPoolName(), dataSource);
        return ds.getCurrentDataSources().keySet();
    }

    @DeleteMapping
    @ApiOperation("删除数据源")
    public String remove(String name) {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        ds.removeDataSource(name);
        return "删除成功";
    }


    /*============================================================================*/
    @Autowired
    private CommonMapper4Mysql mapper;
    @Autowired
    private TableInfoService infoService;


    @GetMapping("/getTableInfo")
    @ApiOperation("获取表信息（测试方法）")
    public String test() {
        return JSON.toJSONString(infoService.info("user"));
    }

    @GetMapping("/datasource")
    @ApiOperation("手动指定数据源")
    public String datasource(String datasource) {
        return JSON.toJSONString(infoService.info(datasource, "user"));
    }

    @GetMapping("/testStreamData")
    @ApiOperation("测试数据库流使查询")
    public void testStreamData() {
        infoService.testStreamData();
    }

}
