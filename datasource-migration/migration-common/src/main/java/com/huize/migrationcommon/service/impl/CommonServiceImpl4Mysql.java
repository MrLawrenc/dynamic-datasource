package com.huize.migrationcommon.service.impl;

import com.huize.migrationcommon.entity.TableInfo;
import com.huize.migrationcommon.mapper.CommonMapper4Mysql;
import com.huize.migrationcommon.service.CommonService4Mysql;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * @author hz20035009-逍遥
 * date   2020/7/3 15:49
 */
@Service
@AllArgsConstructor
public class CommonServiceImpl4Mysql implements CommonService4Mysql {

    private final CommonMapper4Mysql commonMapper;


    @Override
    public List<TableInfo> tableInfoList(String table) {
        List<TableInfo> infos = commonMapper.tableInfoList(table);
        infos.sort(Comparator.comparing(TableInfo::getColumnOrder));
        return infos;
    }
}