package com.huize.migrationwriter;

import com.alibaba.fastjson.JSON;
import com.huize.migrationcommon.anno.DataSourceSwitch;
import com.huize.migrationcommon.writer.Writer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * @author hz20035009-逍遥
 * date   2020/6/11 17:38
 */
@Component
@Slf4j
@DataSourceSwitch("mysql_writer")
public class MySqlWriter implements Writer {
    @Override
    public List<String> tableConstruct() {
        return null;
    }

    @Override
    public void write(List<Collection<String>> rows) {
        log.info("write data : {}", JSON.toJSONString(rows));
    }
}