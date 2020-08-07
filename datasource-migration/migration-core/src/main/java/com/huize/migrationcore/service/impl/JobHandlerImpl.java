package com.huize.migrationcore.service.impl;

import com.github.mrlawrenc.filter.standard.Invoker;
import com.huize.migrationcommon.entity.Command0;
import com.huize.migrationcommon.entity.Job;
import com.huize.migrationcommon.reader.Reader;
import com.huize.migrationcommon.service.CommonService4Mysql;
import com.huize.migrationcommon.writer.Writer;
import com.huize.migrationcore.channel.DataChannel;
import com.huize.migrationcore.entity.JobContext;
import com.huize.migrationcore.entity.JobExecResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author hz20035009-逍遥
 * date   2020/8/6 14:02
 * <p>
 * 数据入库或者删除
 */
@Component
@Slf4j
public class JobHandlerImpl implements Invoker<JobContext, JobExecResult> {

    @Autowired
    private DataChannel channel;
    private int writeMaxNum = 300;
    @Autowired
    private CommonService4Mysql service4Mysql;

    @Override
    public JobExecResult doInvoke(JobContext jobContext) {
        log.info("start handle job id={}", jobContext.getJob());
        Job job = jobContext.getJob();

        Reader reader = jobContext.getReader();
        Writer writer = jobContext.getWriter();

        List<Collection<Object>> rowList = new ArrayList<>();
        List<Long> idxList = new ArrayList<>();
        reader.init(null, row -> {
            //数据到达回调通知

            //加入内存
            long offer = channel.offer(row);
            rowList.add(row);
            idxList.add(offer);

            if (rowList.size() >= writeMaxNum) {
                writer.write(job.getTargetTable(), rowList);
                rowList.clear();
                //释放内存
                boolean release = channel.release(offer);
            }

        });

        //正式读
        reader.read(job);


        reader.destroy(null);

        Command0 currentCommand = job.getCurrentCommand();
        JobExecResult jobExecResult = new JobExecResult();
        if (currentCommand == Command0.READ_WRITE_DEL) {
            jobExecResult.setDelReaderData(true);
        }
        return jobExecResult;
    }

}
