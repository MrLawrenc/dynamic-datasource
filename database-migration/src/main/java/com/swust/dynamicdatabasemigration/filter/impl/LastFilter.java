package com.swust.dynamicdatabasemigration.filter.impl;

import com.swust.dynamicdatabasemigration.filter.Config;
import com.swust.dynamicdatabasemigration.filter.FilterChain;
import com.swust.dynamicdatabasemigration.filter.Request;
import com.swust.dynamicdatabasemigration.filter.Response;
import com.swust.dynamicdatabasemigration.filter.standard.Filter;
import org.springframework.stereotype.Component;

/**
 * @author : hz20035009-逍遥
 * @date : 2020/5/14 9:42
 * @description : 对于出站来说是第一个filter，对于入站则是最后一个filter
 * <p>
 * 留给子类扩展，默认空实现
 */
@Component
public class LastFilter implements Filter {
    @Override
    public void init(Config filterConfig) {

    }

    @Override
    public void doFilter(Request request, Response response, FilterChain chain) {

    }

    @Override
    public void destroy() {

    }
}