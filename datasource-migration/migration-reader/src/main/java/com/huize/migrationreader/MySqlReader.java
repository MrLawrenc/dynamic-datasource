package com.huize.migrationreader;

import com.huize.migrationcommon.anno.DataSourceFlag;
import com.huize.migrationcommon.entity.Command;
import com.huize.migrationcommon.entity.ContextConfig;
import com.huize.migrationcommon.entity.Job;
import com.huize.migrationcommon.mapper.CommonMapper4Mysql;
import com.huize.migrationcommon.reader.Reader;
import org.apache.ibatis.session.ResultContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author hz20035009-逍遥
 * date   2020/6/11 17:38
 */
@Component
@DataSourceFlag(datasourceName = "mysql_reader")
public class MySqlReader implements Reader {

    @Autowired
    private CommonMapper4Mysql mapper4Mysql;
    private AtomicBoolean done = new AtomicBoolean(false);
    private ResultContext<? extends Map<String, String>> resultContext;

    @Override
    public Command command() {
        return null;
    }

    @Override
    public void init(ContextConfig contextConfig) {

    }


    //预读取
    @Override
    public List<Map<String, String>> read(Job job) {
        mapper4Mysql.streamsSelect(job.getSourceTable(), job.getCondition(), resultContext -> this.resultContext = resultContext);
        return null;
    }

    //实际读取每一条记录
    @Override
    public Collection<String> doRead() {
        return resultContext.getResultObject().values();
    }


    @Override
    public boolean done() {
        return done.get();
    }

    @Override
    public void destroy(ContextConfig contextConfig) {

    }

    @Override
    public List<String> tableConstruct() {
        return null;
    }
}