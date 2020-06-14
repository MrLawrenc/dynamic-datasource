package com.huize.migrationcommon;

import com.huize.migrationcommon.entity.Command;
import com.huize.migrationcommon.entity.Job;
import com.huize.migrationcommon.entity.JobInfo;

/**
 * @author : MrLawrenc
 * date  2020/6/13 21:14
 */
public class JobBuilder {
    private final Job job = new Job();


    public JobBuilder jobInfo(JobInfo jobInfo) {
        job.setCron(jobInfo.getCron());
        job.setLastDate(jobInfo.getLastTime());
        job.setCondition(jobInfo.getWhereCondition());
        job.setCron(jobInfo.getCron());
        job.setSourceName(jobInfo.getSourceName());
        job.setTargetName(jobInfo.getTargetName());
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