package com.huize.migrationcore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huize.migrationcommon.entity.JobInfoConfig;
import com.huize.migrationcore.mapper.JobInfoMapper;
import com.huize.migrationcore.service.JobInfoService;
import org.springframework.stereotype.Service;

/**
 * @author : MrLawrenc
 * date  2020/6/13 19:39
 */
@Service
public class JobInfoServiceImpl extends ServiceImpl<JobInfoMapper, JobInfoConfig> implements JobInfoService {
}