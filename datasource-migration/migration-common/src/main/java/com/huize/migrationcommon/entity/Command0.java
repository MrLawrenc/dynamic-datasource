package com.huize.migrationcommon.entity;

import lombok.Getter;

/**
 * @author : MrLawrenc
 * date  2020/6/12 22:39
 * <p>
 * 命令
 */
public enum Command0 {


    READ_WRITE("读取reader数据源的数据，并写入writer数据源"), READ_WRITE_DEL("读取reader数据源的数据，并写入writer数据源，最后需要删除reader数据源的已迁移的数据");

    Command0(String desc) {
        this.desc = desc;
    }


    @Getter
    private String desc;

}