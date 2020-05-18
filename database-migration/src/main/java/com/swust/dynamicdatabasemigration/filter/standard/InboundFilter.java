package com.swust.dynamicdatabasemigration.filter.standard;

import com.swust.dynamicdatabasemigration.filter.FilterChain;
import com.swust.dynamicdatabasemigration.filter.Request;
import com.swust.dynamicdatabasemigration.filter.Response;

/**
 * @author : hz20035009-逍遥
 * @date : 2020/5/18 17:57
 * @description : 入站过滤器
 */
public abstract class InboundFilter implements Filter {
    @Override
    public void doFilter(Request request, Response response, FilterChain chain) {
        doInboundFilter(request, chain);
    }

    public abstract void doInboundFilter(Request request, FilterChain chain);
}