package com.huize.migrationcommon.entity;

import com.huize.migrationcommon.JobBuilder;
import lombok.Data;

import java.util.Date;

/**
 * @author : MrLawrenc
 * date  2020/6/13 10:07
 * <p>
 * 一次迁移任务信息
 */
@Data
public class Job {
    /**
     * cron 表达式
     */
    private String cron;
    /**
     * 上次执行任务时间
     */
    private Date lastDate;

    private String sourceName;
    private String targetName;
    private String sourceTable;
    private String targetTable;

    /**
     * 查询条件,指where后的子句
     */
    private String condition;

    /**
     * 迁移成功是否删除 迁移成功的源数据
     */
    private boolean delSource;
    /**
     * 删除数据的 where 子句，会优先使用主键，如 id in(1,2,3,4)
     */
    private boolean delCondition;


    private Command0 currentCommand;


    private ExecuteResult<?> result;
    //todo .......


    /**
     * 下一个任务，通常为关联表的任务
     */
    private Job nextJob;

    public static JobBuilder createBuilder() {
        return new JobBuilder();
    }

    public static class Entry {

    }

}