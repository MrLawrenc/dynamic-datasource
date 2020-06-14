package com.huize.migrationcore.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author : MrLawrenc
 * date  2020/6/12 23:52
 * <p>
 * 时间轮配置
 */
@Configuration
@Data
public class HashedWheelTimerConfig {

    @Value("${tickDuration:1}")
    private long tickDuration;

    @Value("${ticksPerWheel:65356}")
    private int ticksPerWheel;
}