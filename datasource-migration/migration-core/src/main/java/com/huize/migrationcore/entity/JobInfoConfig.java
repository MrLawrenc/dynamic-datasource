package com.huize.migrationcore.entity;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author : MrLawrenc
 * date  2020/6/14 22:24
 */
@Data
public class JobInfoConfig {
    @NotEmpty
    private String sourceName;
    @NotEmpty
    private String targetName;


    private List<String> tableName;

    private List<String> whereCondition;

    @NotNull
    private Date lastTime;

    @NotEmpty
    private String cron;
}