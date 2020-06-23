package com.huize.migrationcore.utils;

import com.huize.migrationcommon.reader.Reader;
import com.huize.migrationcommon.writer.Writer;
import com.huize.migrationcore.entity.DataSourceConfig;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author : MrLawrenc
 * date  2020/6/13 23:56
 * <p>
 * 保存映射关系
 */
@Data
@Component
public class GlobalMapping {

    /**
     * key：数据源名称
     * value：具体的数据源配置
     */
    private Map<String, DataSourceConfig> sourceMap;
    /**
     * key：所有的数据源名称
     * value：具体的reader实现，该reader适用于key所对应的数据源
     */
    private Map<String, Reader> readerMap;
    private Map<String, Writer> writerMap;
}