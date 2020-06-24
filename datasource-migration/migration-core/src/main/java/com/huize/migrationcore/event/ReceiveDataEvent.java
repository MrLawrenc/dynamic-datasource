package com.huize.migrationcore.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author hz20035009-逍遥
 * date   2020/6/24 16:44
 * <p>
 * 数据到达通知
 */
public class ReceiveDataEvent extends ApplicationEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public ReceiveDataEvent(Object source) {
        super(source);
    }
}