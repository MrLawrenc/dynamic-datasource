package com.huize.migrationcore.controller.config;

import com.huize.migrationcommon.entity.JobInfo;
import com.huize.migrationcore.entity.JobInfoConfig;
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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public boolean addJob(@Validated @RequestBody JobInfoConfig jobInfoConfig) {
        List<String> tableName = jobInfoConfig.getTableName();
        List<String> whereCondition = jobInfoConfig.getWhereCondition();

        List<JobInfo> infos = IntStream.range(0, jobInfoConfig.getTableName().size()).mapToObj(idx -> {
            JobInfo jobInfo = new JobInfo().setCron(jobInfoConfig.getCron())
                    .setSourceName(jobInfoConfig.getSourceName())
                    .setTargetName(jobInfoConfig.getTargetName())
                    .setCreateTime(LocalDateTime.now());
            return jobInfo.setTableName(tableName.get(idx)).setWhereCondition(whereCondition.get(idx));
        }).collect(Collectors.toList());
        boolean success = jobInfoService.saveBatch(infos);
        return success;
    }
}