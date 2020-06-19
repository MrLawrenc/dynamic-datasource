package com.huize.migrationcore;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.strategy.LoadBalanceDynamicDataSourceStrategy;
import com.huize.migrationcommon.Reader;
import com.huize.migrationcommon.anno.DataSourceFlag;
import com.huize.migrationcommon.trans.DataChannel;
import com.huize.migrationcore.schedule.CoreBossEventLoop;
import com.huize.migrationcore.service.DataSourceService;
import com.huize.migrationcore.service.JobInfoService;
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
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

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
    private CoreBossEventLoop eventLoop;

    @Autowired
    private List<Reader> readerList;

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
     /*   mapping.setSourceMap(dataSourceService.list().stream()
                .collect(Collectors.toMap(DataSourceConfig::getName, po -> po)));

        //保存数据源和reader、writer的映射关系
        Map<String, Reader> readerMap = readerList.stream().collect(Collectors.toMap(this::getReaderKey, reader -> reader));
        mapping.setReaderMap(readerMap);


        //添加数据源
        //DataSourceUtil.addDataSource()

        //从数据库查找任务配置
        DynamicDataSourceContextHolder.push("master");
        List<JobInfo> jobInfos = jobInfoService.list();
        if (CollectionUtils.isEmpty(jobInfos)) {
            log.warn("No executable scheduled tasks");
            return;
        }

        //任务初始化
        jobInfos.forEach(jobInfo -> {
            Job job = Job.createBuilder()
                    .jobInfo(jobInfo)
                    .command(Command.CommandKind.READ, Command.OperationType.READER)
                    .build();

            //添加第一波任务
            eventLoop.getWheelTimer().newTimeout(timeout -> {
                        MySqlReader sqlReader = new MySqlReader();

                        //任务拆为若干
                        for (int i = 0; i < 2; i++) {
                            //wait()
                            List<Map<String, String>> list = sqlReader.read(job);

                            channel.offer("lmy", "user", new ArrayList<Row>(), 1024);
                        }

                    }, eventLoop.parseCron4Delay(job.getCron()
                    , new Date())
                    , TimeUnit.SECONDS);
        });

        //
        DynamicDataSourceContextHolder.clear();*/
    }

    /**
     * 获取reader对应的数据源key
     *
     * @param reader reader
     * @return 数据源key
     */
    private String getReaderKey(Reader reader) {
        DataSourceFlag sourceFlag = reader.getClass().getAnnotation(DataSourceFlag.class);
        Assert.notNull(sourceFlag, String.format("reader(%s) must have @DataSourceFlag ", reader.getClass()));
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
