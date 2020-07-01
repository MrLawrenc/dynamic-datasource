package com.huize.migrationcommon.reader;

import com.huize.migrationcommon.NotifyWriterListener;
import com.huize.migrationcommon.WriterReader;
import com.huize.migrationcommon.entity.Command;
import com.huize.migrationcommon.entity.ContextConfig;
import com.huize.migrationcommon.entity.Job;

import java.util.Collection;

/**
 * @author : MrLawrenc
 * date  2020/6/12 23:19
 */
public interface Reader extends WriterReader {


    /**
     * 当前reader对应命令，如全量读
     *
     * @return 命令
     */
    Command command();

    /**
     * 读之前的初始化工作
     *
     * @param contextConfig 配置
     */
    void init(ContextConfig contextConfig, NotifyWriterListener listener);


    /**
     * 预读取数据，会执行查询，但数据不会立马到达jvm内存
     *
     * @param job 任务信息
     * @return 结果集大小
     */
    void read(Job job);

    Collection<String> doRead();

    boolean done();

    /**
     * 销毁方法，当一个job结束时调用，如销毁数据源
     *
     * @param contextConfig 配置
     */
    void destroy(ContextConfig contextConfig);
}