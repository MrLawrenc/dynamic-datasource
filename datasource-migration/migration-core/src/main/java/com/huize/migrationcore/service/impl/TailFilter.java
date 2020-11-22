package com.huize.migrationcore.service.impl;

import com.github.mrlawrenc.filter.config.Config;
import com.github.mrlawrenc.filter.entity.Response;
import com.github.mrlawrenc.filter.service.FilterChain;
import com.github.mrlawrenc.filter.standard.OutboundFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author hz20035009-逍遥
 * date   2020/8/6 14:05
 */
@Component
@Slf4j
public class TailFilter extends OutboundFilter {


    @Override
    public void init(Config filterConfig) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public FilterChain doOutboundFilter(Response response, FilterChain chain) {
     /*   if (response.isDelReaderData()) {
            log.info("need to perform the next subTask(del reader data)");
        }*/
        return chain;
    }
}