package com.example.dynamicdatasource.controller;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.*;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.example.dynamicdatasource.entity.User;
import com.example.dynamicdatasource.mappper.UserMapper;
import com.example.dynamicdatasource.service.ServiceImpl;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : MrLawrenc
 * @date : 2020/5/15 23:28
 * @description : TODO
 */
@RestController
@AllArgsConstructor
@Api(value = "测试")
public class TestController {
    private final DataSource dataSource;
    private final DataSourceCreator dataSourceCreator;
    private final BasicDataSourceCreator basicDataSourceCreator;
    private final JndiDataSourceCreator jndiDataSourceCreator;
    private final DruidDataSourceCreator druidDataSourceCreator;
    private final HikariDataSourceCreator hikariDataSourceCreator;

    private final UserMapper userMapper;


    private final ServiceImpl service;

    @ApiOperation("测试lmy数据源查询入")
    @ApiOperationSupport(author = "mars@huize.com")
    @GetMapping("/lmy")
    public List<User> lmy() {
        return service.lmyList();
    }

    @ApiOperation("测试master数据源查询入")
    @GetMapping("/master")
    public List<User> master() {
        return service.masterList();
    }

    @ApiOperation("测试lmy数据源插入")
    @GetMapping("/lmyAdd")
    public void lmyAdd() {
        service.lmyAdd();
    }

    @ApiOperation("测试master数据源插入")
    @GetMapping("/masterAdd")
    public void masterAdd() {
        service.masterAdd();
    }

    @GetMapping("/dbInfo")
    @ApiOperation("获取lmy数据源的所有表名")
    public List<String> getCurrentDBInfo() {
        DynamicDataSourceContextHolder.push("lmy");
        List<String> list = userMapper.info("user").stream()
                .map(m -> m.get("COLUMN_NAME")).collect(Collectors.toList());
        DynamicDataSourceContextHolder.clear();
        return list;
    }


    @GetMapping("/connection-test")
    @ApiOperation("测试两次获取的connection是否一样")
    public boolean connectionTest() throws Exception {
        DataSource lmy = ((DynamicRoutingDataSource) dataSource).getDataSource("lmy");
        Connection connection = lmy.getConnection();
        Connection connection1 = lmy.getConnection();
        System.out.println(connection + "  " + connection1);
        boolean r = connection == connection1;
        //fix 必须关闭 否则不会释放
        connection.close();
        connection1.close();
        return r;
    }

    public static void main(String[] args) {
        //0 0 10,14,16 * * ?
        CronSequenceGenerator generator = new CronSequenceGenerator("22 22 2 * * ?");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date next = generator.next(new Date());
        System.out.println("下次执行时间:" + sdf.format(next));
        Date nextNext = generator.next(next);
        System.out.println("下下次执行时间:" + sdf.format(nextNext));
        Date nextNextNext = generator.next(nextNext);
        System.out.println("下下下次执行时间:" + sdf.format(nextNextNext));
    }
}