package com.huize.migrationcore.channel;

import java.util.Collection;

/**
 * @author : MrLawrenc
 * date  2020/6/14 10:49
 * <p>
 * 数据缓冲区
 */
public abstract class DataChannel {


    public abstract long offer(Collection<Object> row);

    public abstract boolean isFull();

    public abstract boolean release(long idx);

    public abstract boolean release(long[] idx);

}