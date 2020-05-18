package com.example.dynamicdatasource;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author : MrLawrenc
 * @date : 2020/5/16 16:41
 * @description :   https://doc.xiaominfo.com/knife4j
 * <p>
 * 地址：ip:port/doc.html
 * <p>
 * <p>
 * {@link EnableSwagger2} 该注解是Springfox-swagger框架提供的使用Swagger注解，该注解必须加
 * {@link EnableKnife4j} 该注解是knife4j提供的增强注解, Ui提供了例如动态参数、参数过滤、接口排序等增强功能,
 * 如果你想使用这些增强功能就必须加该注解，否则可以不用加
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
public class SwaggerAutoConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(ApiInfo.DEFAULT)
                //.groupName("2.0.1")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example"))
                .paths(PathSelectors.any())
                .build();
    }
}