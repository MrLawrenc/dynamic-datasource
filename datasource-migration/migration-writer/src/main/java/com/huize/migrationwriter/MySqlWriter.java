package com.huize.migrationwriter;

import com.huize.migrationcommon.anno.DataSourceFlag;
import com.huize.migrationcommon.writer.Writer;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * @author hz20035009-逍遥
 * date   2020/6/11 17:38
 */
@Component
@DataSourceFlag(datasourceName = "mysql_writer")
public class MySqlWriter implements Writer {
    @Override
    public List<String> tableConstruct() {
        return null;
    }

    @Override
    public void write(Collection<String> row) {

    }
}