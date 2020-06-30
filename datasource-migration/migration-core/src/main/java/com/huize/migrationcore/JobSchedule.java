package com.huize.migrationcore;

import com.huize.migrationcommon.entity.Job;
import com.huize.migrationcommon.reader.Reader;
import com.huize.migrationcommon.writer.Writer;
import com.huize.migrationcore.channel.DataChannel;
import com.huize.migrationcore.utils.GlobalMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author hz20035009-逍遥
 * date   2020/6/30 10:53
 */
@Component
@Slf4j
public class JobSchedule {
    @Autowired
    private GlobalMapping mapping;
    @Autowired
    private DataChannel channel;

    /**
     * 提交任务
     *
     * @param job 具体的某一次任务
     */
    public void submitJob(Job job) {
        Reader reader = mapping.getReaderMap().get(job.getSourceName());
        Writer writer = mapping.getWriterMap().get(job.getTargetName());


        //step 1 表结构比对
        if (reader.tableConstruct() != writer.tableConstruct()) {
            log.error("");
        }

        //预读
        reader.read(job);

        //实际读
        Collection<String> row = reader.doRead();

        //加入缓存
        channel.offer( row);


        //写入
        writer.write(row);
    }
}