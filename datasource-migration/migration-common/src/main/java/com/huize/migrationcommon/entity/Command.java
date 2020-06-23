package com.huize.migrationcommon.entity;

import lombok.Data;

/**
 * @author : MrLawrenc
 * date  2020/6/12 22:39
 * <p>
 * 对应每一次任务 多操作构成链表
 */
@SuppressWarnings("all")
@Data
public class Command {

    private CommandKind kind;
    private OperationType type;
    private Command next;


    public Command(CommandKind kind, OperationType type) {
        this.kind = kind;
        this.type = type;
    }

    /**
     * 命令种类
     */
    public static enum CommandKind {
        READ((byte) 1, "read data"), WRITE((byte) 2, "write data"),
        DELETE_BY_PRIMARY((byte) 3, "del by primary key");

        CommandKind(byte code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        private byte code;
        private String desc;

    }

    /**
     * 操作 读/写 数据源
     */
    public static enum OperationType {
        WRITEER((byte) 1, "target is writer"), READER((byte) 2, "target is reader");

        OperationType(byte type, String desc) {
            this.type = type;
            this.desc = desc;
        }

        private byte type;
        private String desc;
    }
}