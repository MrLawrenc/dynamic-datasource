package com.huize.migrationcommon.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author hz20035009-逍遥
 * date   2020/6/11 14:22
 */
@Data
public class DataSourceInfo {

    @NotBlank
    @ApiModelProperty(value = "连接池名称", example = "test")
    private String poolName;

    @NotBlank
    @ApiModelProperty(value = "JDBC driver", example = "org.h2.Driver")
    private String driverClassName;

    @NotBlank
    @ApiModelProperty(value = "JDBC url 地址", example = "jdbc:h2:mem:test10")
    private String url;

    @NotBlank
    @ApiModelProperty(value = "JDBC 用户名", example = "sa")
    private String username;

    @ApiModelProperty(value = "JDBC 密码")
    private String password;
}
