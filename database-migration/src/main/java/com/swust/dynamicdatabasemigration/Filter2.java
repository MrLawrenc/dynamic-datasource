package com.swust.dynamicdatabasemigration;

import com.github.mrLawrenc.filter.config.Config;
import com.github.mrLawrenc.filter.entity.Response;
import com.github.mrLawrenc.filter.service.FilterChain;
import com.github.mrLawrenc.filter.standard.OutboundFilter;

/**
 * @author : hz20035009-逍遥
 * @date : 2020/5/19 15:52
 * @description : TODO
 */
public class Filter2 extends OutboundFilter {

    @Override
    public void init(Config filterConfig) {
        System.out.println("out init");
    }

    @Override
    public void destroy() {
        System.out.println("out destroy");
    }

    @Override
    public FilterChain doOutboundFilter(Response response, FilterChain chain) {
        System.out.println("do out f2...");
        return chain;
    }
}