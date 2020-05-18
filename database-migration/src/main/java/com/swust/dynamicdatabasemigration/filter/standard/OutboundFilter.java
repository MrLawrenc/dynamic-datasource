package com.swust.dynamicdatabasemigration.filter.standard;

import com.swust.dynamicdatabasemigration.filter.FilterChain;
import com.swust.dynamicdatabasemigration.filter.Request;
import com.swust.dynamicdatabasemigration.filter.Response;

/**
 * @author : hz20035009-逍遥
 * @date : 2020/5/18 17:57
 * @description : 出站过滤器
 */
public abstract class OutboundFilter implements Filter {
    @Override
    public void doFilter(Request request, Response response, FilterChain chain) {
        doOutboundFilter(response, chain);
    }

    public abstract void doOutboundFilter(Response response, FilterChain chain);
}