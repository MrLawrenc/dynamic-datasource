package com.huize.migrationcommon;

import java.util.List;

/**
 * @author hz20035009-逍遥
 * date   2020/6/22 16:52
 */
public interface WriterReader {

    /**
     * 表结构信息
     * <p>
     * 暂时只比对表名
     */
    List<String> tableConstruct();
}
