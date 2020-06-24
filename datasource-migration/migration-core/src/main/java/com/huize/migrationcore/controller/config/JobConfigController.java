package com.huize.migrationcore.controller.config;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceCreator;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.druid.DruidConfig;
import com.huize.migrationcommon.entity.DataSourceInfo;
import com.huize.migrationcommon.entity.JobInfoConfig;
import com.huize.migrationcore.entity.DataSourceConfig;
import com.huize.migrationcore.entity.JobInfo;
import com.huize.migrationcore.service.DataSourceService;
import com.huize.migrationcore.service.JobInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.time.LocalDateTime;
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
    private final DataSourceService dataSourceService;

    private final javax.sql.DataSource dataSource;
    private final DataSourceCreator dataSourceCreator;
  //  private final DruidDataSourceCreator druidDataSourceCreator;

    @PostMapping("/addDatasourceConfig")
    @ApiOperation("添加一个数据源")
    public boolean addDataSource(@Validated @RequestBody DataSourceInfo dataSourceInfo) {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;

        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        BeanUtils.copyProperties(dataSourceInfo, dataSourceProperty);
        DruidConfig druidConfig = new DruidConfig();
        druidConfig.setInitialSize(1).setMaxActive(2);
        dataSourceProperty.setDruid(druidConfig);
        DataSource currentDataSource = dataSourceCreator.createDataSource(dataSourceProperty);
        ds.addDataSource(dataSourceInfo.getPoolName(), currentDataSource);


        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        BeanUtils.copyProperties(dataSourceInfo, dataSourceConfig);
        dataSourceConfig.setName(dataSourceInfo.getPoolName()).setCreateTime(LocalDateTime.now());

        return dataSourceService.save(dataSourceConfig);
    }

    @PostMapping("/add")
    @ApiOperation("添加一个迁移任务")
    public boolean addJob(@Validated @RequestBody JobInfo jobInfo) {
        //ResponseEntity
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
            int parentId = parentJob.getId();
            JobInfo.RelevancyTable relevancyTable = jobInfo.getRelevancyTable();
            while (Objects.nonNull(relevancyTable)) {
                JobInfoConfig job = JSON.parseObject(JSON.toJSONString(parentJob), JobInfoConfig.class);
                String tableName = relevancyTable.getTableName();
                job.setSelectCondition(relevancyTable.getFieldName()).setTargetTableName(tableName).setSourceTableName(tableName);

                jobInfoService.save(job.setParentId(parentId).setCategory(parentJob.getId()));

                parentId = job.getId();
                relevancyTable = relevancyTable.getRelevancyTable();
            }
        }

        return success;
    }
}