package com.huize.migrationcore.service.impl;

import com.github.mrlawrenc.filter.config.Config;
import com.github.mrlawrenc.filter.service.FilterChain;
import com.github.mrlawrenc.filter.standard.OutboundFilter;
import com.huize.migrationcore.entity.JobContext;
import com.huize.migrationcore.entity.JobExecResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author hz20035009-逍遥
 * date   2020/8/6 14:05
 */
@Component
@Slf4j
public class TailFilter extends OutboundFilter<JobContext, JobExecResult> {

    @Override
    public FilterChain doOutboundFilter(JobExecResult response, FilterChain chain) {
        if (response.isDelReaderData()) {
            log.info("need to perform the next subTask(del reader data)");
        }
        return chain;
    }

    @Override
    public void init(Config filterConfig) {

    }

    @Override
    public void destroy() {

    }
}