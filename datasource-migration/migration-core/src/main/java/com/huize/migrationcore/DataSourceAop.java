package com.huize.migrationcore;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author hz20035009-逍遥
 * date   2020/6/11 18:12
 */
@Aspect
public class DataSourceAop {

    @Pointcut("@annotation(com.huize.migrationcore.anno.DataSourceSwitch)")
    public void intercept() {
    }

    @Around("intercept()")
    public Object around(ProceedingJoinPoint joinPoint) {
        System.out.println("《【环绕】方法之前：前置通知");

        try {
            return joinPoint.proceed();//执行方法

        } catch (Throwable e) {
            System.out.println("《【环绕】发生异常时：异常通知");
        } finally {
            System.out.println("《【环绕】最终通知");
        }
        return "";
    }
}