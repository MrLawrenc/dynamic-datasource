package com.huize.migrationcore;

import com.alibaba.fastjson.JSON;
import com.huize.migrationcommon.entity.Command0;
import com.huize.migrationcommon.entity.Job;
import com.huize.migrationcommon.reader.Reader;
import com.huize.migrationcommon.writer.Writer;
import com.huize.migrationcore.channel.DataChannel;
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
    private GlobalMapping mapping;
    @Autowired
    private DataChannel channel;

    private int writeMaxNum = 2;

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

            Reader reader = mapping.getReaderMap().get(job.getSourceName());
            Writer writer = mapping.getWriterMap().get(job.getTargetName());

            Command0 currentCommand = job.getCurrentCommand();


            //step 1 表结构比对
            if (reader.tableConstruct() != writer.tableConstruct()) {
                log.error("table construct not same");
            }

            List<Collection<String>> rowList = new ArrayList<>();
            List<Long> idxList = new ArrayList<>();
            reader.init(null, row -> {

                //数据到达回调通知
                System.out.println("row:" + JSON.toJSONString(row));
                //加入内存
                long offer = channel.offer(row);
                rowList.add(row);
                idxList.add(offer);

                if (rowList.size()>=writeMaxNum){
                    writer.write(rowList);
                    //释放内存
                    boolean release = channel.release(offer);
                }

            });

            //正式读
            reader.read(job);


            reader.destroy(null);


        }).whenComplete((v, t) -> {
            if (t != null) {
                t.printStackTrace();
            }
        });
    }


}