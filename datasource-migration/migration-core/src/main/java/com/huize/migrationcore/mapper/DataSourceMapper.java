package com.huize.migrationcore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huize.migrationcore.entity.DataSourceConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : MrLawrenc
 * date  2020/6/13 19:36
 */
@Mapper
public interface DataSourceMapper extends BaseMapper<DataSourceConfig> {
}