package com.huize.migrationcore;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.strategy.LoadBalanceDynamicDataSourceStrategy;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author hz20035009-逍遥
 * date   2020/6/11 14:22
 */
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@Slf4j
@MapperScan({"com.huize.migrationcore.mapper", "com.huize.migrationcommon.mapper", "com.huize.migrationreader", "com.huize.migrationwriter"})
public class MigrationCoreApplication implements CommandLineRunner {

    @Value("${server.port:8080}")
    private int port;

    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(MigrationCoreApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        DynamicRoutingDataSource routingDataSource = (DynamicRoutingDataSource) this.dataSource;
        //默认就是该策略，该策略对分组生效，即slave_1 slave_2都属于slave组
        routingDataSource.setStrategy(new LoadBalanceDynamicDataSourceStrategy());

        log.info("current doc url : http://{}:{}/doc.html", getHostAddr(), port);

        //DynamicDataSourceContextHolder.push("master");
        //DynamicDataSourceContextHolder.clear();

    }

    public String getHostAddr() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return address.getHostAddress();
    }

}
