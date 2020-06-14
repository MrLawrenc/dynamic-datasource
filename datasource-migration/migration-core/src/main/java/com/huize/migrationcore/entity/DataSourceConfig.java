package com.huize.migrationcore.entity;

import com.huize.migrationcommon.entity.DataSourceInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author : MrLawrenc
 * date  2020/6/13 23:27
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DataSourceConfig extends DataSourceInfo {
    private int id;
    private Date createTime;
    private Date updateTime;
}