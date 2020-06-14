package com.huize.migrationcore.entity;

import lombok.Data;

/**
 * @author : MrLawrenc
 * date  2020/6/13 0:14
 */
@Data
public class TimerTask {

    /**
     * cron 表达式
     */
    private String cron;
}