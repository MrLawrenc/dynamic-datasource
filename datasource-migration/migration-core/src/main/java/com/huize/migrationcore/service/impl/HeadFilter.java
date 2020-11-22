package com.huize.migrationcore.service.impl;

import com.github.mrlawrenc.filter.config.Config;
import com.github.mrlawrenc.filter.entity.Request;
import com.github.mrlawrenc.filter.service.FilterChain;
import com.github.mrlawrenc.filter.standard.InboundFilter;
import com.huize.migrationcore.utils.GlobalMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author hz20035009-逍遥
 * date   2020/8/6 14:04
 */
@Component
@Order()
public class HeadFilter extends InboundFilter {
    @Autowired
    private GlobalMapping mapping;


    @Override
    public void init(Config filterConfig) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public FilterChain doInboundFilter(Request request, FilterChain chain) {
       /* Job job = jobContext.getJob();


        Reader reader = jobContext.getReader();
        Writer writer = jobContext.getWriter();
        //step 1 表结构比对
        List<TableInfo> readerInfo = reader.tableConstruct(job.getSourceTable());
        List<TableInfo> writerInfo = writer.tableConstruct(job.getTargetTable());
        for (int i = 0; i < writerInfo.size(); i++) {
            if (!writerInfo.get(i).equals(readerInfo.get(i))) {
                jobContext.getError().setContinue(false).setMsg("table info mismatch");
                return chain.skip2Service();
            }
        }*/
        return chain;
    }
}