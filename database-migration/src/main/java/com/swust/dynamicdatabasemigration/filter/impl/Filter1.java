package com.swust.dynamicdatabasemigration.filter.impl;


import com.swust.dynamicdatabasemigration.filter.Config;
import com.swust.dynamicdatabasemigration.filter.FilterChain;
import com.swust.dynamicdatabasemigration.filter.Request;
import com.swust.dynamicdatabasemigration.filter.Response;
import com.swust.dynamicdatabasemigration.filter.standard.Filter;
import org.springframework.core.annotation.Order;

/**
 * @author : MrLawrenc
 * @date : 2020/5/13 22:45
 * @description : 具体的过滤器
 */
@Order(3)
public class Filter1 implements Filter {
    @Override
    public void init(Config filterConfig) {
        System.out.println("过滤器1 init...........");
    }

    @Override
    public void doFilter(Request request, Response response, FilterChain chain) {
        System.out.println("过滤器1 doFilter...........");
    }

    @Override
    public void destroy() {
        System.out.println("过滤器1 destroy...........");
    }
}