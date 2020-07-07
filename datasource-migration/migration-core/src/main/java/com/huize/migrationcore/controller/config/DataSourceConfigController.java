package com.huize.migrationcore.controller.config;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.*;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.google.common.collect.Lists;
import com.huize.migrationcommon.entity.DataSourceInfo;
import com.huize.migrationcommon.entity.TableInfo;
import com.huize.migrationcommon.mapper.CommonMapper4Mysql;
import com.huize.migrationcommon.service.CommonService4Mysql;
import com.huize.migrationreader.service.TableInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


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
    public String test(String tableName) {
        return JSON.toJSONString(infoService.info(tableName));
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

    @Autowired
    private CommonMapper4Mysql commonMapper4Mysql;
    @Autowired
    private CommonService4Mysql service4Mysql;

    @GetMapping("/save")
    @ApiOperation("测试数据库插入，查询当前数据库所有数据，之后并更改索引之后原样插入，参数为当前数据库最大索引")
    public void save(int maxIdx) {
        DynamicDataSourceContextHolder.push("mysql_reader");
        List<Collection<Object>> rows = new ArrayList<>();
        //表结构信息,根据字段排序
        List<TableInfo> infos = service4Mysql.tableInfoList("user");


        commonMapper4Mysql.streamsSelect("user", " 1=1 ", r -> {
            Map<String, Object> resultObject = r.getResultObject();
            resultObject.put("id",maxIdx+ Integer.parseInt(resultObject.get("id").toString()));
            List<Object> row = infos.stream().map(info -> {
                String columnName = info.getColumnName();
                return resultObject.get(columnName);
            }).collect(Collectors.toList());
            rows.add(row);
        });
        infoService.save("mysql_reader", "user", rows);
        DynamicDataSourceContextHolder.clear();

      /*  List<List<Object>> rows = new ArrayList<>();
        int id=54;
        for (int i = 0; i < 45; i++) {
            List<Object> row = new ArrayList<>();
            row.add(id++);
            row.add("名字"+i);
            row.add("pwd"+i);
            row.add("real name"+i);
            row.add("img");

            row.add("2");
            row.add(new java.sql.Date(System.currentTimeMillis()));
            row.add(new java.sql.Date(System.currentTimeMillis()));
            rows.add(row);
        }

        infoService.save("mysql_reader", "user", rows);*/
    }
    @GetMapping("/testUpdate")
    @ApiOperation("测试数据库更新")
    public void testUpdate() {
        DynamicDataSourceContextHolder.push("mysql_writer");
        ArrayList<Object> primaryIds = new ArrayList<>();
        primaryIds.add(1);
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("user_name");
        columnNames.add("password");
        columnNames.add("real_name");
        columnNames.add("is_del");
        columnNames.add("create_time");
        columnNames.add("del_time");
        ArrayList<Object> row = new ArrayList<>();
        row.add("更新之后的名字");
        row.add("更新之后的pwd");
        row.add("更新之后的real_name");
        row.add(1);
        row.add(LocalDate.now());
        row.add(LocalDate.now());
        List<Collection<Object>> rows=new ArrayList<>();
        rows.add(row);

        service4Mysql.update("user","id",primaryIds,columnNames,rows);
        infoService.testStreamData();
    }
    public static void main(String[] args) {
        ArrayList<Object> objects = Lists.newArrayList();
        objects.add(111);
        objects.add(null);
        System.out.println(objects.size());
    }
}
