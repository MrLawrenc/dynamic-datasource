package com.huize.migrationcore.utils;

import com.huize.migrationcommon.Reader;
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
@Data@Component
public class GlobalMapping {

    private Map<String, DataSourceConfig> sourceMap;
    private  Map<String, Reader> readerMap;
}