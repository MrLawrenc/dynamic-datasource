package com.huize.migrationcore;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.strategy.LoadBalanceDynamicDataSourceStrategy;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.mrLawrenc.filter.config.EnableFilterAndInvoker;
import com.google.common.base.Preconditions;
import com.huize.migrationcommon.WriterReader;
import com.huize.migrationcommon.anno.DataSourceSwitch;
import com.huize.migrationcommon.entity.Command0;
import com.huize.migrationcommon.entity.Job;
import com.huize.migrationcommon.entity.JobInfoConfig;
import com.huize.migrationcommon.reader.Reader;
import com.huize.migrationcommon.writer.Writer;
import com.huize.migrationcore.entity.DataSourceConfig;
import com.huize.migrationcore.schedule.CoreContext;
import com.huize.migrationcore.service.DataSourceService;
import com.huize.migrationcore.service.JobInfoService;
import com.huize.migrationcore.utils.DateUtil;
import com.huize.migrationcore.utils.GlobalConstant;
import com.huize.migrationcore.utils.GlobalMapping;
import com.huize.migrationcore.utils.ProxyUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author hz20035009-逍遥
 * date   2020/6/11 14:22
 */
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@Slf4j
@ComponentScan(basePackages = {"com.huize", "com.huize.migrationreader", "com.huize.migrationwriter"})
@MapperScan({"com.huize.migrationcore.mapper",
        "com.huize.migrationcommon.mapper",
        "com.huize.migrationreader.mapper",
        "com.huize.migrationwriter.mapper"})
@EnableFilterAndInvoker
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
    private JobSchedule jobSchedule;


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
        DynamicDataSourceContextHolder.push(GlobalConstant.MASTER);
        List<JobInfoConfig> jobInfos = jobInfoService.list(new QueryWrapper<JobInfoConfig>().isNull("parent_id"));
        if (CollectionUtils.isEmpty(jobInfos)) {
            log.warn("No executable scheduled tasks");
            return;
        }

        //任务初始化
        jobInfos.forEach(jobInfo -> {
            long delay = DateUtil.parseCron4Delay(jobInfo.getCron(), new Date());
            log.info("job {} delay {} s", jobInfo.getId(), delay);
            //添加第一波任务
            eventLoop.getWheelTimer().newTimeout(timeout -> {

                Job job = buildJobChain(jobInfo);

                //提交任务到调度中心
                jobSchedule.submitJob(job);

            }, delay, TimeUnit.SECONDS);
        });

        DynamicDataSourceContextHolder.clear();
    }

    /**
     * 获取reader对应的数据源key
     *
     * @param writerReader writer和reader具体实现类
     * @return 数据源key
     */
    private String getDataSourceKey(WriterReader writerReader) {
        if (AopUtils.isAopProxy(writerReader)) {
            log.info("current clz({}) is proxy obj", writerReader.getClass());
            try {
                if (AopUtils.isJdkDynamicProxy(writerReader)) {
                    writerReader = (WriterReader) ProxyUtil.getJdkDynamicProxyTargetObject(writerReader);
                } else {
                    //cglib
                    writerReader = (WriterReader) ProxyUtil.getCglibProxyTargetObject(writerReader);
                }
            } catch (Exception ignored) {
            }
        }

        DataSourceSwitch sourceFlag = writerReader.getClass().getAnnotation(DataSourceSwitch.class);
        Preconditions.checkNotNull(sourceFlag, String.format("reader(%s) must have @DataSourceFlag ", writerReader.getClass()));
        String value = sourceFlag.value();
        if (StringUtils.isEmpty(value)) {
            value = sourceFlag.value();
        }
        /*Assert.isTrue(mapping.getSourceMap().containsKey(value),
                String.format("current datasource(%s) map  not contains current reader datasource(%s)"
                        , JSON.toJSONString(mapping.getSourceMap().keySet()), value));*/
        return value;
    }


    /**
     * 构造job链
     *
     * @param parentJobConfig 顶层任务
     * @return job链的头结点
     */
    public Job buildJobChain(JobInfoConfig parentJobConfig) {
        Job parentJob = Job.createBuilder()
                .jobInfo(parentJobConfig)
                .command(Command0.READ_WRITE_DEL)
                .build();

        //根据父级任务获取所有关联表的子任务
        List<JobInfoConfig> childJobList = jobInfoService.list(new QueryWrapper<JobInfoConfig>().eq("category", parentJobConfig.getId()));

        Map<Integer, JobInfoConfig> jobInfoConfigMap = childJobList.stream().collect(Collectors.toMap(JobInfoConfig::getParentId, job -> job));

        Job currentJob = parentJob;
        JobInfoConfig currentNode = jobInfoConfigMap.get(parentJobConfig.getId());
        while (Objects.nonNull(currentNode)) {
            Job temp = Job.createBuilder()
                    .jobInfo(currentNode)
                    .command(Command0.READ_WRITE_DEL)
                    .build();
            currentJob.setNextJob(temp);

            currentJob = temp;
            currentNode = jobInfoConfigMap.get(currentNode.getId());
        }
        return parentJob;
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
