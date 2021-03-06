package com.huize.migrationcommon;

import com.huize.migrationcommon.entity.Command0;
import com.huize.migrationcommon.entity.Job;
import com.huize.migrationcommon.entity.JobInfoConfig;

import java.util.Objects;

/**
 * @author : MrLawrenc
 * date  2020/6/13 21:14
 */
public class JobBuilder {
    private final Job job = new Job();


    public JobBuilder jobInfo(JobInfoConfig jobInfoConfig) {
        job.setCron(jobInfoConfig.getCron());
        job.setLastDate(jobInfoConfig.getLastTime());
        job.setCondition(jobInfoConfig.getSelectCondition());
        job.setCron(jobInfoConfig.getCron());
        job.setSourceName(jobInfoConfig.getSourceName());
        job.setTargetName(jobInfoConfig.getTargetName());
        job.setSourceTable(jobInfoConfig.getSourceTableName());
        job.setTargetTable(jobInfoConfig.getTargetTableName());
        job.setDbId(jobInfoConfig.getId());
        return this;
    }

    public JobBuilder command(Command0 command0) {
        job.setCurrentCommand(command0);
        return this;
    }

    public JobBuilder addLast(Job next) {
        Job lastJob = job.getNextJob();
        while (Objects.nonNull(lastJob)) {
            lastJob = lastJob.getNextJob();
        }

        return this;
    }

    public Job build() {
        return job;
    }

}