package com.swust.dynamicdatabasemigration;

import com.github.mrLawrenc.filter.config.Config;
import com.github.mrLawrenc.filter.entity.Request;
import com.github.mrLawrenc.filter.service.FilterChain;
import com.github.mrLawrenc.filter.standard.InboundFilter;

/**
 * @author : hz20035009-逍遥
 * @date : 2020/5/19 15:52
 * @description : TODO
 */
public class Filter1 extends InboundFilter {
    @Override
    public FilterChain doInboundFilter(Request request, FilterChain chain) {
        System.out.println("do f1...");
        return chain;
    }

    @Override
    public void init(Config filterConfig) {

    }

    @Override
    public void destroy() {

    }
}