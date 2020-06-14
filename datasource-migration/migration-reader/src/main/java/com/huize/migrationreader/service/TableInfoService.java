package com.huize.migrationreader.service;

import com.huize.migrationcommon.anno.DataSourceSwitch;
import com.huize.migrationcommon.mapper.TableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author : MrLawrenc
 * date  2020/6/12 20:27
 */
@Service
public class TableInfoService {
    @Autowired
    private TableMapper tableMapper;

    @DataSourceSwitch("tableName")
    public List<Map<String, String>> info(String tableName) {
        List<Map<String, String>> user = tableMapper.info("user");
        System.out.println(user);
        return user;
    }

    @DataSourceSwitch()
    public List<Map<String, String>> info(String datasource, String tableName) {
        List<Map<String, String>> user = tableMapper.info("user");
        System.out.println(user);
        return user;
    }

}