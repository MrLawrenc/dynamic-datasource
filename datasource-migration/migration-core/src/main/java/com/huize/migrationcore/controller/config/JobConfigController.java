package com.huize.migrationcore.controller.config;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.huize.migrationcommon.entity.JobInfoConfig;
import com.huize.migrationcore.entity.JobInfo;
import com.huize.migrationcore.service.JobInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author : MrLawrenc
 * date  2020/6/15 12:20
 */
@RestController
@AllArgsConstructor
@RequestMapping("/job")
@Api(tags = "迁移任务配置")
public class JobConfigController {

    private final JobInfoService jobInfoService;


    @PostMapping("/add")
    @ApiOperation("添加一个迁移任务")
    public boolean addJob(@Validated @RequestBody JobInfo jobInfo) {
        ArrayList<JobInfoConfig> jobs = Lists.newArrayList();

        String cron = jobInfo.getCron();
        String sourceName = jobInfo.getSourceName();
        String targetName = jobInfo.getTargetName();
        String whereCondition = jobInfo.getWhereCondition();

        String sourceTable = jobInfo.getSourceTable();
        String targetTable = jobInfo.getTargetTable();

        //构建父级任务
        JobInfoConfig parentJob = new JobInfoConfig();
        parentJob.setSelectCondition(whereCondition).setSourceName(sourceName).setTargetName(targetName)
                .setCreateTime(LocalDateTime.now()).setCron(cron).setSourceTableName(sourceTable)
                .setTargetTableName(targetTable);
        boolean success = jobInfoService.save(parentJob);
        if (success) {
            int parentId=parentJob.getId();
            JobInfo.RelevancyTable relevancyTable = jobInfo.getRelevancyTable();
            while (Objects.nonNull(relevancyTable)) {
                JobInfoConfig job = JSON.parseObject(JSON.toJSONString(parentJob), JobInfoConfig.class);
                String tableName = relevancyTable.getTableName();
                job.setSelectCondition(relevancyTable.getFieldName()).setTargetTableName(tableName).setSourceTableName(tableName);

                jobInfoService.save(job.setParentId(parentId));

                parentId=job.getId();
                relevancyTable = relevancyTable.getRelevancyTable();
            }
        }

        return success;
    }
}