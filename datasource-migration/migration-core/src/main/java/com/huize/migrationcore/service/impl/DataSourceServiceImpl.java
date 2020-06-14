package com.huize.migrationcore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huize.migrationcore.entity.DataSourceConfig;
import com.huize.migrationcore.mapper.DataSourceMapper;
import com.huize.migrationcore.service.DataSourceService;
import org.springframework.stereotype.Service;

/**
 * @author : MrLawrenc
 * date  2020/6/13 19:39
 */
@Service
public class DataSourceServiceImpl extends ServiceImpl<DataSourceMapper, DataSourceConfig> implements DataSourceService {
}