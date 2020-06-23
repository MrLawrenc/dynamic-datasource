package com.huize.migrationcore;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.strategy.LoadBalanceDynamicDataSourceStrategy;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huize.migrationcommon.WriterReader;
import com.huize.migrationcommon.anno.DataSourceFlag;
import com.huize.migrationcommon.entity.Command;
import com.huize.migrationcommon.entity.Job;
import com.huize.migrationcommon.entity.JobInfoConfig;
import com.huize.migrationcommon.entity.Row;
import com.huize.migrationcommon.reader.Reader;
import com.huize.migrationcommon.trans.DataChannel;
import com.huize.migrationcommon.writer.Writer;
import com.huize.migrationcore.entity.DataSourceConfig;
import com.huize.migrationcore.schedule.CoreContext;
import com.huize.migrationcore.service.DataSourceService;
import com.huize.migrationcore.service.JobInfoService;
import com.huize.migrationcore.utils.DateUtil;
import com.huize.migrationcore.utils.GlobalMapping;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author hz20035009-逍遥
 * date   2020/6/11 14:22
 */
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@Slf4j
@ComponentScan(basePackages = {"com.huize.migrationcore", "com.huize"})
@MapperScan({"com.huize.migrationcore.mapper",
        "com.huize.migrationcommon.mapper",
        "com.huize.migrationreader.mapper",
        "com.huize.migrationwriter.mapper"})
public class MigrationCoreApplication implements CommandLineRunner {

    @Value("${server.port:8080}")
    private int port;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JobInfoService jobInfoService;
    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private CoreContext eventLoop;

    @Autowired
    private List<Reader> readerList;
    @Autowired
    private List<Writer> writerList;

    @Autowired
    private GlobalMapping mapping;

    @Autowired
    private DataChannel channel;


    public static void main(String[] args) {
        SpringApplication.run(MigrationCoreApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        DynamicRoutingDataSource routingDataSource = (DynamicRoutingDataSource) this.dataSource;
        //默认就是该策略，该策略对分组生效，即slave_1 slave_2都属于slave组
        routingDataSource.setStrategy(new LoadBalanceDynamicDataSourceStrategy());

        log.info("current doc url : http://{}:{}/doc.html", getHostAddr(), port);


        //保存数据源映射关系
        mapping.setSourceMap(dataSourceService.list().stream()
                .collect(Collectors.toMap(DataSourceConfig::getName, po -> po)));

        //保存数据源和reader、writer的映射关系
        Map<String, Reader> readerMap = readerList.stream().collect(Collectors.toMap(this::getDataSourceKey, reader -> reader));
        mapping.setReaderMap(readerMap);
        Map<String, Writer> writerMap = writerList.stream().collect(Collectors.toMap(this::getDataSourceKey, writer -> writer));
        mapping.setWriterMap(writerMap);


        //todo 初始化所有数据源


        //从数据库查找任务配置
        DynamicDataSourceContextHolder.push("master");
        List<JobInfoConfig> jobInfos = jobInfoService.list(new QueryWrapper<JobInfoConfig>().isNull("parent_id"));
        if (CollectionUtils.isEmpty(jobInfos)) {
            log.warn("No executable scheduled tasks");
            return;
        }

        //任务初始化
        jobInfos.forEach(jobInfo -> {


            //添加第一波任务
            eventLoop.getWheelTimer().newTimeout(timeout -> {

                        //根据父级任务获取所有关联表的子任务
                        List<JobInfoConfig> childJobList = jobInfoService.list(new QueryWrapper<JobInfoConfig>().eq("parent_id", jobInfo.getId()));

                        if (Objects.isNull(childJobList)) {
                            log.info("single table ");


                        } else {
                            log.info("mutilate table ");
                        }


                        Job job = Job.createBuilder()
                                .jobInfo(jobInfo)
                                .command(Command.CommandKind.READ, Command.OperationType.READER)
                                .build();

                        Reader reader = mapping.getReaderMap().get(job.getSourceName());
                        Writer writer = mapping.getWriterMap().get(job.getSourceName());

                        if (reader.tableConstruct() != writer.tableConstruct()) {
                            log.error("");
                        }

                        //读取
                        List<Map<String, String>> list = reader.read(job);

                        channel.offer("lmy", "user", new ArrayList<Row>(), 1024);

                    }, DateUtil.parseCron4Delay(jobInfo.getCron()
                    , new Date())
                    , TimeUnit.SECONDS);
        });

        //
        DynamicDataSourceContextHolder.clear();
    }

    /**
     * 获取reader对应的数据源key
     *
     * @param writerReader writer和reader具体实现类
     * @return 数据源key
     */
    private String getDataSourceKey(WriterReader writerReader) {
        DataSourceFlag sourceFlag = writerReader.getClass().getAnnotation(DataSourceFlag.class);
        Assert.notNull(sourceFlag, String.format("reader(%s) must have @DataSourceFlag ", writerReader.getClass()));
        String value = sourceFlag.value();
        if (StringUtils.isEmpty(value)) {
            value = sourceFlag.datasourceName();
        }
        Assert.isTrue(mapping.getSourceMap().containsKey(value),
                String.format("current datasource(%s) map  not contains current reader datasource(%s)"
                        , JSON.toJSONString(mapping.getSourceMap().keySet()), value));
        return value;
    }


    public String getHostAddr() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        assert address != null;
        return address.getHostAddress();
    }

}
