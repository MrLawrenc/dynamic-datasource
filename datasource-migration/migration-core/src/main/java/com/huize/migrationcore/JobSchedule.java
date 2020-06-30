package com.huize.migrationcore;

import com.github.mrLawrenc.filter.entity.Request;
import com.github.mrLawrenc.filter.entity.Response;
import com.github.mrLawrenc.filter.standard.Invoker;
import com.huize.migrationcommon.entity.Command0;
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
public class JobSchedule implements Invoker {
    @Autowired
    private GlobalMapping mapping;
    @Autowired
    private DataChannel channel;

    /**
     * 提交任务
     *
     * @param job 具体的某一次任务
     *            <p>
     *            暂时读任务使用一个线程，写任务使用一个线程
     */
    public void submitJob(Job job) {
        Reader reader = mapping.getReaderMap().get(job.getSourceName());
        Writer writer = mapping.getWriterMap().get(job.getTargetName());

        Command0 currentCommand = job.getCurrentCommand();
        if (currentCommand == Command0.READ_WRITE) {

        } else if (currentCommand == Command0.READ_WRITE_DEL) {

        } else {
            log.error("current command({}) not find ", currentCommand.getDesc());
        }


        //step 1 表结构比对
        if (reader.tableConstruct() != writer.tableConstruct()) {
            log.error("");
        }

        //预读
        reader.read(job);

        //实际读
        Collection<String> row = reader.doRead();

        //加入内存
        long offer = channel.offer(row);


        //写入
        writer.write(row);

        //释放内存
        boolean release = channel.release(offer);
    }

    @Override
    public Response doInvoke(Request request) {
        return null;
    }
}