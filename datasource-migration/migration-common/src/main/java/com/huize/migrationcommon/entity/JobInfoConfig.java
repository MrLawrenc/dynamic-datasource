package com.huize.migrationcommon.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author : MrLawrenc
 * date  2020/6/13 19:37
 * <p>
 * 数据库任务实体
 */
@Data
@Accessors(chain = true)
public class JobInfoConfig {

    private int id;
    private String sourceName;
    private String targetName;


    private String targetTableName;
    private String sourceTableName;

    /**
     * 没有关联表存在代表where子句条件，有关联表存在，代表关联字段名
     */
    private String selectCondition;

    /**
     * 兼容cron解析
     */
    private Date lastTime;

    private String cron;

    private LocalDateTime createTime;

    /**
     * 当存在关联表关系的时候，会有parentId存在
     */
    private Integer parentId;


    /**
     * 所属分类任务id，即关联表的顶层表任务的id
     */
    private Integer category;

}