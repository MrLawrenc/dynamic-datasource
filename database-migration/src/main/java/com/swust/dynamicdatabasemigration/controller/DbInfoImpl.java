package com.swust.dynamicdatabasemigration.controller;

import com.swust.dynamicdatabasemigration.controller.mapper.MyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author : MrLawrenc
 * date  2020/6/10 23:35
 */
@Service
public class DbInfoImpl implements DbInfo {
    @Autowired
    private MyMapper mapper;

    @Override
    public List<Map<String, String>> selectTableInfo(String datasource, String table) {
        List<Map<String, String>> info = mapper.info(table);
        //todo 做一些转换记录等

        System.out.println("数据转换.......");
        System.out.println("记录.......");

        return info;
    }
}