package com.swust.dynamicdatabasemigration;

import com.github.mrLawrenc.filter.config.Config;
import com.github.mrLawrenc.filter.entity.Request;
import com.github.mrLawrenc.filter.entity.Response;
import com.github.mrLawrenc.filter.service.FilterChain;
import com.github.mrLawrenc.filter.service.FirstFilter;
import org.springframework.stereotype.Component;

/**
 * @author : hz20035009-逍遥
 * @date : 2020/5/19 15:52
 * @description : TODO
 */
@Component
public class Filter1 extends FirstFilter {

    @Override
    public void init(Config filterConfig) {
        System.out.println("first..init..");
    }

    @Override
    public FilterChain doFilter(Request request, Response response, FilterChain chain) {
        System.out.println("..first do .....");
        return super.doFilter(request, response, chain);
    }

    @Override
    public void destroy() {
        System.out.println("first..destroy..");
    }
}