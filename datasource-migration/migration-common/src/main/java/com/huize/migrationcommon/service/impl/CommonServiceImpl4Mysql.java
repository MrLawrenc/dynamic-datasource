package com.huize.migrationcommon.service.impl;

import com.alibaba.fastjson.JSON;
import com.huize.migrationcommon.entity.TableInfo;
import com.huize.migrationcommon.mapper.CommonMapper4Mysql;
import com.huize.migrationcommon.service.CommonService4Mysql;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * @author hz20035009-逍遥
 * date   2020/7/3 15:49
 */
@Service
@Slf4j
@AllArgsConstructor
public class CommonServiceImpl4Mysql implements CommonService4Mysql {

    private final CommonMapper4Mysql commonMapper;


    @Override
    public List<TableInfo> tableInfoList(String table) {
        List<TableInfo> infos = commonMapper.tableInfoList(table);
        infos.sort(Comparator.comparing(TableInfo::getColumnOrder));
        return infos;
    }

    @Override
    public boolean save(String table, List<Collection<Object>> rows) {
        log.info("write data : {}", JSON.toJSONString(rows));
        commonMapper.save(table, rows);
        return false;
    }

    @Override
    public boolean update(String table, String primaryKey,
                          List<Object> primaryValues,
                          List<String> columnNames,
                          List<Collection<Object>> columnValues) {

        return commonMapper.update(table,primaryKey,primaryValues,columnNames,columnValues);
    }
}