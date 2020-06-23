package com.huize.migrationcore.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author hz20035009-逍遥
 * date   2020/6/19 15:01
 * <p>
 * 数据处理完成之后会发送的事件,可用于writer完成之后更新缓存大小
 */
@Component
public class DealDoneEvent extends ApplicationEvent {

    /**
     * 行的idx集合
     */
    @Getter
    private List<String> idxList;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public DealDoneEvent(List<String> source) {
        super(source);
    }


}