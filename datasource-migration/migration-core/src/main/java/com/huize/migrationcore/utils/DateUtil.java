package com.huize.migrationcore.utils;


import org.springframework.scheduling.support.CronSequenceGenerator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * @author hz20035009-逍遥
 * date   2020/6/22 10:14
 */
public final class DateUtil {


    private static final String DEFAULT_FORMAT = "yyyy:MM:dd hh:mm:ss";
    /**
     * 默认东8区
     */
    private static final String DEFAULT_OFFSET_ID = "+8";


    /**
     * 时间戳-->Date
     */
    public static String timestampToDateStr(Long timestamp) {
        return timestampToDateStr(timestamp, DEFAULT_FORMAT);
    }

    public static String timestampToDateStr(Long timestamp, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(new Date(timestamp));
    }

    /**
     * 时间戳-->LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Long timestamp) {
        return new Date(timestamp).toInstant().atOffset(ZoneOffset.of(DEFAULT_OFFSET_ID)).toLocalDateTime();
    }

    /**
     * 时间戳-->LocalDate
     */
    public static LocalDate toLocalDate(Long timestamp) {
        return new Date(timestamp).toInstant().atOffset(ZoneOffset.of(DEFAULT_OFFSET_ID)).toLocalDate();
    }

    /**
     * 获取秒
     */
    public static Long second(LocalDateTime localDateTime) {
        return localDateTime.toEpochSecond(ZoneOffset.of(DEFAULT_OFFSET_ID));
    }

    /**
     * 获取毫秒
     */
    public static Long milliSecond(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.of(DEFAULT_OFFSET_ID)).toEpochMilli();
    }


    /**
     * 将java.util.Date 转换为 LocalDateTime,默认时区为东8区
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atOffset(ZoneOffset.of(DEFAULT_OFFSET_ID)).toLocalDateTime();
    }

    public static LocalDate toLocalDate(Date date) {
        return date.toInstant().atOffset(ZoneOffset.of(DEFAULT_OFFSET_ID)).toLocalDate();
    }


    /**
     * 将LocalDateTime 转换为 java.util.Date，默认时区为东8区
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.toInstant(ZoneOffset.of(DEFAULT_OFFSET_ID)));
    }


    /**
     * 根据cron表达式和上次任务执行时间，计算出下次任务执行的延时时间
     *
     * @param cron     cron
     * @param lastDate 上次任务执行时间
     * @return 延时时间 单位秒
     */
    public static long parseCron4Delay(String cron, Date lastDate) {
        CronSequenceGenerator generator = new CronSequenceGenerator(cron);
        Date nextDate = generator.next(lastDate);
        return (nextDate.getTime() - lastDate.getTime()) / 1000;
    }

}