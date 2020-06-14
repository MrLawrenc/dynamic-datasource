package com.huize.migrationcommon.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author : MrLawrenc
 * date  2020/6/13 19:37
 */
@Data
public class JobInfo {

    private int id;
    private String sourceName;
    private String targetName;

    private String whereCondition;

    private Date lastTime;

    private String cron;
}