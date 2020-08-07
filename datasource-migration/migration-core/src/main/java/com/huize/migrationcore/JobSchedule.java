package com.huize.migrationcore;

import com.alibaba.fastjson.JSON;
import com.github.mrlawrenc.filter.entity.Response;
import com.huize.migrationcommon.entity.Command0;
import com.huize.migrationcommon.entity.Job;
import com.huize.migrationcommon.reader.Reader;
import com.huize.migrationcommon.writer.Writer;
import com.huize.migrationcore.entity.JobContext;
import com.huize.migrationcore.service.impl.JobHandlerImpl;
import com.huize.migrationcore.utils.GlobalMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author hz20035009-逍遥
 * date   2020/6/30 10:53
 */
@Component
@Slf4j
public class JobSchedule {


    @Autowired
    private JobHandlerImpl jobHandler;

    @Autowired
    private GlobalMapping mapping;

    /**
     * 提交任务
     *
     * @param job 具体的某一次任务
     *            <p>
     *            暂时读任务使用一个线程，写任务使用一个线程
     */
    public void submitJob(Job job) {
        log.info("start job : {}", JSON.toJSONString(job));
        CompletableFuture.runAsync(() -> {

            Command0 currentCommand = job.getCurrentCommand();

            Reader reader = mapping.getReaderMap().get(job.getSourceName());
            Writer writer = mapping.getWriterMap().get(job.getTargetName());
            Response response = jobHandler.doInvoke(new JobContext().setJob(job).setReader(reader).setWriter(writer));

            if (true) {
                return;
            }


        }).whenComplete((v, t) -> {
            if (t != null) {
                t.printStackTrace();
            }
        });
    }

    /**
     * 所有列类型转为String
     *
     * @param row           原始列
     * @param changeDataIdx 需要改变类型的索引值
     * @return str列
     */
    private Collection<String> typeChange2Str(Collection<Object> row, int[] changeDataIdx) {
        int size = row.size();
        List<String> newRow = new ArrayList<>(size);

        int idx = 0;
        int changeIdx = 0;
        for (Object data : row) {
            if (idx == changeDataIdx[changeIdx]) {

            } else {
                newRow.add(data.toString());
            }

            idx++;
            changeIdx++;
        }

        return newRow;
    }


}