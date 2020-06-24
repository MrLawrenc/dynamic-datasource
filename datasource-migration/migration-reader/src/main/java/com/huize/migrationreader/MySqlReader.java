package com.huize.migrationreader;

import com.huize.migrationcommon.anno.DataSourceFlag;
import com.huize.migrationcommon.entity.Command;
import com.huize.migrationcommon.entity.ContextConfig;
import com.huize.migrationcommon.entity.Job;
import com.huize.migrationcommon.mapper.CommonMapper4Mysql;
import com.huize.migrationcommon.reader.Reader;
import com.huize.migrationcommon.trans.DataChannel;
import org.apache.ibatis.session.ResultContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    @Autowired
    private DataChannel channel;
    private AtomicBoolean done = new AtomicBoolean(false);
    private ResultContext<? extends Map<String, String>> resultContext;

    @Override
    public Command command() {
        return null;
    }

    @Override
    public void init(ContextConfig contextConfig) {

    }


    @Override
    public List<Map<String, String>> read(Job job) {
        done.set(false);
        mapper4Mysql.streamsSelect(job.getSourceTable(), job.getCondition(), resultContext -> this.resultContext = resultContext);
        return null;
    }

    @Override
    public void doRead(String datasourceName, String table) {
        if (!resultContext.isStopped()) {
            Map<String, String> resultObject = resultContext.getResultObject();
            channel.offer(datasourceName, table, resultObject);
        } else {
            done.compareAndSet(false, true);
        }
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