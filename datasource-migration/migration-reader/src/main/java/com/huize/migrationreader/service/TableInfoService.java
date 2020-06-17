package com.huize.migrationreader.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huize.migrationcommon.anno.DataSourceSwitch;
import com.huize.migrationcommon.mapper.TableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @DataSourceSwitch("lmy")
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

    @DataSourceSwitch("lmy")
    @Transactional
    public void testStreamData() {
        QueryWrapper<Map<String, String>> queryWrapper = new QueryWrapper<>();
        tableMapper.getNeedSignOffUserCheckRecord(resultContext -> {
            Map<String, String> checkRecordEntity = resultContext.getResultObject();
            System.out.println("每一条记录:" + JSON.toJSONString(checkRecordEntity));
        });


        tableMapper.getNeedSignOffUserCheckRecord(queryWrapper, resultContext -> {
            Map<String, String> checkRecordEntity = resultContext.getResultObject();
            System.out.println("每一条记录:" + JSON.toJSONString(checkRecordEntity));
        });

        //必须保证连接未被关闭
        for (Map<String, String> map : tableMapper.cursorQueryDepartmentAll()) {
            System.out.println("游标获取的每一条数据:" + JSON.toJSONString(map));
        }

    }

}