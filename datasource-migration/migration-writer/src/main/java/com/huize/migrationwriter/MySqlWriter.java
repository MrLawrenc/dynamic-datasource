package com.huize.migrationwriter;

import com.huize.migrationcommon.anno.DataSourceSwitch;
import com.huize.migrationcommon.entity.ContextConfig;
import com.huize.migrationcommon.entity.TableInfo;
import com.huize.migrationcommon.service.CommonService4Mysql;
import com.huize.migrationcommon.writer.Writer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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


    @Autowired
    private CommonService4Mysql service4Mysql;

    @Override
    public List<TableInfo> tableConstruct(String tableName) {
        return service4Mysql.tableInfoList(tableName);
    }

    @Override
    public void init(ContextConfig contextConfig) {
        //获取当前操作表的所有主键数据

    }

    @Override
    public void write(String tableName, List<Collection<Object>> rows) {
        service4Mysql.save(tableName,rows);
    }
}