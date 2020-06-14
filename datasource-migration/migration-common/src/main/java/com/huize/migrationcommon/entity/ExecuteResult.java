package com.huize.migrationcommon.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author : MrLawrenc
 * date  2020/6/13 10:36
 * <p>
 * 每一次命令执行结果
 * @see Command
 */
@Data
@Accessors(chain = true)
public class ExecuteResult<T> {

    private int code;
    private String desc;

    private T data;
}