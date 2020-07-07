package com.huize.migrationcore;

import com.alibaba.fastjson.JSON;
import com.huize.migrationcommon.entity.Command0;
import com.huize.migrationcommon.entity.Job;
import com.huize.migrationcommon.entity.TableInfo;
import com.huize.migrationcommon.reader.Reader;
import com.huize.migrationcommon.service.CommonService4Mysql;
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

    @Autowired
    private CommonService4Mysql service4Mysql;

    private int writeMaxNum = 300;

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
            if (true){
                return;
            }
            Reader reader = mapping.getReaderMap().get(job.getSourceName());
            Writer writer = mapping.getWriterMap().get(job.getTargetName());

            Command0 currentCommand = job.getCurrentCommand();


            //step 1 表结构比对
            List<TableInfo> readerInfo = reader.tableConstruct(job.getSourceTable());
            List<TableInfo> writerInfo = writer.tableConstruct(job.getTargetTable());


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