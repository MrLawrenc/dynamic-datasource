package com.huize.migrationcore.aop;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.huize.migrationcommon.anno.DataSourceSwitch;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.validation.ValidationException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author hz20035009-逍遥
 * date   2020/6/11 18:12
 * <p>
 * 切换数据源
 */
@Aspect
@Slf4j
@Component
public class DataSourceChoose {

    /**
     * 切该注解标记的所有方法
     */
    @Pointcut("@annotation(com.huize.migrationcommon.anno.DataSourceSwitch)")
    public void methodIntercept() {
    }

    /**
     * 切所有标记该注解的类
     */
    @Pointcut("@within(com.huize.migrationcommon.anno.DataSourceSwitch)")
    public void intercept() {
    }

    @Around("intercept() || methodIntercept()")
    //@Around(" methodIntercept()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Object target = joinPoint.getTarget();

        Method method = signature.getMethod();
        log.info("enter joinPoint,clz:{} method:{}", target.getClass(), method.getName());
        //只切部分reader和writer方法
        DataSourceSwitch clzAnnotation = target.getClass().getAnnotation(DataSourceSwitch.class);
        if (Objects.nonNull(clzAnnotation)) {
            if (method.getName().contains("init")) {
                log.info("current method({}) return", method);
                return joinPoint.proceed();
            } else {
                String source = clzAnnotation.value();
                DynamicDataSourceContextHolder.push(source);
                try {
                    return joinPoint.proceed();
                } finally {
                    DynamicDataSourceContextHolder.clear();
                }
            }

        } else {

            String dataSource = method.getAnnotation(DataSourceSwitch.class).value();
            if (StringUtils.isEmpty(dataSource)) {
                Object[] sourceParam = joinPoint.getArgs();
                if (sourceParam.length == 0 || !(sourceParam[0] instanceof String)) {
                    throw new ValidationException("Parameter verification failed," + target.getClass() + "#" +
                            method.getName() + "At least one parameter must be a string!");
                }
                dataSource = sourceParam[0].toString();
            }
            log.info("current datasource : {}", dataSource);
            DynamicDataSourceContextHolder.push(dataSource);
            try {
                return joinPoint.proceed();
            } finally {
                DynamicDataSourceContextHolder.clear();
            }
        }
    }
}