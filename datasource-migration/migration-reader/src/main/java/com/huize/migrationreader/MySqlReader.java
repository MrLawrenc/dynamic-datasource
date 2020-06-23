package com.huize.migrationreader;

import com.huize.migrationcommon.reader.Reader;
import com.huize.migrationcommon.anno.DataSourceFlag;
import com.huize.migrationcommon.entity.Command;
import com.huize.migrationcommon.entity.ContextConfig;
import com.huize.migrationcommon.entity.Job;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author hz20035009-逍遥
 * date   2020/6/11 17:38
 */
@Component
@DataSourceFlag(datasourceName = "mysql_reader")
public class MySqlReader implements Reader {
    @Override
    public Command command() {
        return null;
    }

    @Override
    public void init(ContextConfig contextConfig) {

    }

    @Override
    public List<String> columnInfo() {
        return null;
    }

    @Override
    public List<Map<String, String>> read(Job job) {
        return null;
    }

    @Override
    public void destroy(ContextConfig contextConfig) {

    }

    @Override
    public List<String> tableConstruct() {
        return null;
    }
}