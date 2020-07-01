package com.huize.migrationreader;

import com.huize.migrationcommon.NotifyWriterListener;
import com.huize.migrationcommon.anno.DataSourceSwitch;
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
@DataSourceSwitch("mysql_reader")
public class MySqlReader implements Reader {

    @Autowired
    private CommonMapper4Mysql mapper4Mysql;
    private AtomicBoolean done = new AtomicBoolean(false);
    private ResultContext<? extends Map<String, String>> resultContext;

    private NotifyWriterListener listener;

    @Override
    public Command command() {
        return null;
    }

    @Override
    public void init(ContextConfig contextConfig, NotifyWriterListener listener) {
        this.listener = listener;
    }


    //预读取
    @Override
    public void read(Job job) {
        mapper4Mysql.streamsSelect(job.getSourceTable(), job.getCondition(), resultContext -> {
            Map<String, String> rowMap = resultContext.getResultObject();
            listener.sendData(rowMap.values());
        });
    }

    //实际读取每一条记录
    @Override
    public Collection<String> doRead() {
        if (resultContext.isStopped()) {
            return null;
        }
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