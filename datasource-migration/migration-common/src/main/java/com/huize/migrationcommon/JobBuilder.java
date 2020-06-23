package com.huize.migrationcommon;

import com.huize.migrationcommon.entity.Command;
import com.huize.migrationcommon.entity.Job;
import com.huize.migrationcommon.entity.JobInfoConfig;

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
        return this;
    }

    public JobBuilder command(Command.CommandKind kind, Command.OperationType operationType) {
        job.setCurrentCommand(new Command(kind, operationType));
        return this;
    }

    public Job build() {
        return job;
    }

}