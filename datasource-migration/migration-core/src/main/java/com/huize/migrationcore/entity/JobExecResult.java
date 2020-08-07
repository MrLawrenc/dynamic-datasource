package com.huize.migrationcore.entity;

import com.github.mrlawrenc.filter.entity.Response;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author hz20035009-逍遥
 * date   2020/8/7 14:41
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class JobExecResult extends Response {
    /**
     * 是否删除迁移成功的数据
     */
    private boolean delReaderData;
}