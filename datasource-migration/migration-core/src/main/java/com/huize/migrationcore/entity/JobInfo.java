package com.huize.migrationcore.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author : MrLawrenc
 * date  2020/6/14 22:24
 */
@Data
public class JobInfo {
    @NotEmpty
    @ApiModelProperty(value = "源数据源名", example = "mysqlReader")
    private String sourceName;
    @NotEmpty
    @ApiModelProperty(value = "目标数据源名", example = "mysqlWriter")
    private String targetName;


    @ApiModelProperty(value = " 源表")
    private String sourceTable;
    @ApiModelProperty(value = "存在关联表则不为null")
    private RelevancyTable relevancyTable;

    @ApiModelProperty(value = "目标表")
    private String targetTable;

    @ApiModelProperty(value = "源表查询的where子句", example = " age>12 ")
    private String whereCondition;


    @ApiModelProperty(value = "定时任务约束cron表达式", example = "* 2 * * * ?")
    @NotEmpty
    private String cron;

    @Data
    public static class RelevancyTable {
        @ApiModelProperty(value = "表名", example = "teacher")
        private String tableName;

        @ApiModelProperty(value = "关联字段名", example = "teacher_id")
        private String fieldName;

        @ApiModelProperty(value = "关联表，若没有，则为null")
        private RelevancyTable relevancyTable;
    }
}