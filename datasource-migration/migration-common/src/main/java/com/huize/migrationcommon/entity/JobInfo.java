package com.huize.migrationcommon.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author : MrLawrenc
 * date  2020/6/13 19:37
 */
@Data
@Accessors(chain = true)
public class JobInfo {

    private int id;
    private String sourceName;
    private String targetName;


    private String tableName;

    private String whereCondition;

    private Date lastTime;

    private String cron;

    private LocalDateTime createTime;
}