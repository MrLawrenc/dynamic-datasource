package com.swust.dynamicdatabasemigration;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.github.mrLawrenc.filter.config.EnableFilterAndInvoker;
import com.github.mrLawrenc.filter.entity.Request;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@EnableFilterAndInvoker@MapperScan("com.swust.dynamicdatabasemigration.controller.mapper")
public class DynamicDatabaseMigrationApplication implements CommandLineRunner {

    @Autowired
    private ServiceInvoker serviceInvoker;
    @Autowired
    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(DynamicDatabaseMigrationApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        System.out.println(serviceInvoker.doInvoke(new Request()));
    }
}
