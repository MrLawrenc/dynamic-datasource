package com.huize.migrationcore.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author : MrLawrenc
 * date  2020/6/13 23:27
 */
@Data@Accessors(chain = true)
public class DataSourceConfig {
    private int id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String name;
    private String driverClassName;
    private String url;
    private String username;
    private String password;
}