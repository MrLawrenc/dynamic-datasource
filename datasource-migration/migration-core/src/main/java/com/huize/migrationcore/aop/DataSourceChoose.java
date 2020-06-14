package com.huize.migrationcore.aop;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.huize.migrationcommon.anno.DataSourceSwitch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.validation.ValidationException;
import java.lang.reflect.Method;

/**
 * @author hz20035009-逍遥
 * date   2020/6/11 18:12
 * <p>
 * 切换数据源
 */
@Aspect
@Component
public class DataSourceChoose {

    @Pointcut("@annotation(com.huize.migrationcommon.anno.DataSourceSwitch)")
    public void intercept() {
    }

    @Around("intercept()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        //被代理对象
        Object target = joinPoint.getTarget();

        Method method = signature.getMethod();
        String dataSource = method.getAnnotation(DataSourceSwitch.class).value();
        if (StringUtils.isEmpty(dataSource)) {
            Object[] sourceParam = joinPoint.getArgs();
            if (sourceParam.length == 0 || !(sourceParam[0] instanceof String)) {
                throw new ValidationException("Parameter verification failed," + target.getClass() + "#" +
                        method.getName() + "At least one parameter must be a string!");
            }
            dataSource = sourceParam[0].toString();
        }

        DynamicDataSourceContextHolder.push(dataSource);
        try {
            return joinPoint.proceed();
        } finally {
            DynamicDataSourceContextHolder.clear();
        }
    }
}