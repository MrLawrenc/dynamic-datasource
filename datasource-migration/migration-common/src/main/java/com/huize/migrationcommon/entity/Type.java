package com.huize.migrationcommon.entity;

/**
 * @author MrLawrenc
 * date  2020/6/14 11:17
 * <p>
 * byte     1字节
 * <p>
 * short    2字节
 * <p>
 * int      4字节
 * <p>
 * long     8字节
 * <p>
 * char     2字节（C语言中是1字节）可以存储一个汉字
 * <p>
 * float    4字节
 * <p>
 * double   8字节
 */
public enum Type {
    NULL(), INT(), LONG(), DOUBLE(), STRING, BOOL, DATE, BYTES;
}