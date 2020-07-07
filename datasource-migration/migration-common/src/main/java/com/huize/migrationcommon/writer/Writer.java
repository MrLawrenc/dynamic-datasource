package com.huize.migrationcommon.writer;

import com.huize.migrationcommon.WriterReader;
import com.huize.migrationcommon.entity.ContextConfig;

import java.util.Collection;
import java.util.List;

/**
 * @author hz20035009-逍遥
 * date   2020/6/22 10:00
 */
public interface Writer extends WriterReader {
    /**
     * 读之前的初始化工作
     *
     * @param contextConfig 配置
     */
    void init(ContextConfig contextConfig);

    void write(String tableName, List<Collection<Object>> rows);
}