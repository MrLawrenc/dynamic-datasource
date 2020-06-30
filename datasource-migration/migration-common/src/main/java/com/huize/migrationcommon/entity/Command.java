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
    private Command next;


    public Command(CommandKind kind) {
        this.kind = kind;
    }

    /**
     * 操作具体命令
     */
    public static enum CommandKind {
        /**
         * 持有该命令的job会操作reader数据源，并进行查询操作
         */
        READER_READ((byte) 1, OperationType.READER)
        /**
         * 持有该命令的job会操作writer数据源，并进行写入操作
         */
        , WRITER_WRITE((byte) 2, OperationType.WRITEER)
        /**
         * 持有该命令的job会操作reader数据源，并根据主键进行删除操作
         */
        , READER_DELETE_BY_PRIMARY((byte) 3, OperationType.READER);

        CommandKind(byte code, OperationType type) {
            this.code = code;
            this.type = type;
        }

        private byte code;

        private OperationType type;

    }

    /**
     * 标记操作的数据源，包括 读/写 数据源
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