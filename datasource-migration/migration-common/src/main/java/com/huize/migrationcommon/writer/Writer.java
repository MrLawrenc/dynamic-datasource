package com.huize.migrationcommon.writer;

import com.huize.migrationcommon.WriterReader;

import java.util.Collection;

/**
 * @author hz20035009-逍遥
 * date   2020/6/22 10:00
 */
public interface Writer extends WriterReader {

    void write(Collection<String> row);
}