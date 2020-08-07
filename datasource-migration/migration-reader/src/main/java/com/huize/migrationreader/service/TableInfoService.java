package com.huize.migrationreader.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huize.migrationcommon.anno.DataSourceSwitch;
import com.huize.migrationcommon.entity.TableInfo;
import com.huize.migrationcommon.mapper.CommonMapper4Mysql;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author : MrLawrenc
 * date  2020/6/12 20:27
 */
@Service
public class TableInfoService {
    @Autowired
    private CommonMapper4Mysql commonMapper4Mysql;

    @DataSourceSwitch("master")
    public List<TableInfo> info(String tableName) {
        List<TableInfo> user = commonMapper4Mysql.tableInfoList(tableName);
        return user;
    }

    @DataSourceSwitch()
    public List<TableInfo> info(String datasource, String tableName) {
        List<TableInfo> user = commonMapper4Mysql.tableInfoList(tableName);
        return user;
    }

    @DataSourceSwitch
    public void save(String datasource, String tableName, List<Collection<Object>> rows) {
        commonMapper4Mysql.save(tableName, rows);
    }

    /**
     * 来源是流式数据，放入缓冲，使用响应式流依次处理
     */
    @DataSourceSwitch("mysql_reader")
    @Transactional
    public void testStreamData() {
        QueryWrapper<Map<String, String>> queryWrapper = new QueryWrapper<>();
        commonMapper4Mysql.streamsSelect("user", " 1=1 ", resultContext -> {

            resultContext.isStopped();
            Map<String, Object> checkRecordEntity = resultContext.getResultObject();
            System.out.println("handler result :" + JSON.toJSONString(checkRecordEntity));
        });

        commonMapper4Mysql.streamsSelect1("user", " 1=1 ", resultContext -> {

            resultContext.isStopped();
            String checkRecordEntity = resultContext.getResultObject();
            System.out.println("handler result :" + JSON.toJSONString(checkRecordEntity));
        });

        Cursor<Map<String, String>> maps = commonMapper4Mysql.cursorQueryDepartmentAll();
        //必须保证连接未被关闭
        for (Map<String, String> map : maps) {
            System.out.println("cursor result : " + JSON.toJSONString(map));
        }

    }

}