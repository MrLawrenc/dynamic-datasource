package com.huize.migrationcore.channel;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author : MrLawrenc
 * date  2020/6/14 10:49
 * <p>
 * 数据缓冲区
 */
@Data
@Component
public abstract class DataChannel {


    public abstract void offer(Collection<String> row);

}