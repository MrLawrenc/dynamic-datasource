package com.huize.migrationcommon;

import com.huize.migrationcommon.entity.Command;
import com.huize.migrationcommon.entity.ContextConfig;
import com.huize.migrationcommon.entity.Job;

import java.util.List;
import java.util.Map;

/**
 * @author : MrLawrenc
 * date  2020/6/12 23:19
 */
public interface Reader {


    /**
     * 当前reader对应得命令，如全量读
     *
     * @return 命令
     */
    Command command();

    /**
     * 读之前的初始化工作
     *
     * @param contextConfig 配置
     */
    void init(ContextConfig contextConfig);


    /**
     * 读取数据
     *
     * @param job 任务信息
     * @return 结果集
     */
    List<Map<String, String>> read(Job job);

    /**
     * 销毁方法，当一个job结束时调用，如销毁数据源
     *
     * @param contextConfig 配置
     */
    void destroy(ContextConfig contextConfig);
}