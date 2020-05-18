package com.example.dynamicdatasource;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.strategy.LoadBalanceDynamicDataSourceStrategy;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;


/**
 * @author : MrLawrenc
 * @date : 2020/5/16 11
 * @description :   TODO
 * <p>
 * 查询本次数据库连接的所有表信息
 * select * from information_schema.TABLES where TABLE_SCHEMA=(select database())
 * <p>
 * 查询本次数据库连接的指定表
 * select * from information_schema.COLUMNS where TABLE_SCHEMA = (select database()) and TABLE_NAME="alarm"
 * <p>
 * 推荐使用select * from information_schema.COLUMNS where TABLE_SCHEMA = (select database())能查询到所有表，所有列信息
 *
 *
 * <b><b/>
 * 注意，若使用druid连接池，则需要排除{@linkplain DruidDataSourceAutoConfigure}配置类，才能使用苞米豆的动态数据源
 */
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@MapperScan("com.example.dynamicdatasource.mappper")
public class DynamicDatasourceApplication implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(DynamicDatasourceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        DynamicRoutingDataSource routingDataSource = (DynamicRoutingDataSource) this.dataSource;
        //默认就是该策略，该策略对分组生效，即slave_1 slave_2都属于slave组
        routingDataSource.setStrategy(new LoadBalanceDynamicDataSourceStrategy());

    }

}
