package com.huize.migrationcore.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hz20035009-逍遥
 * date   2020/6/11 18:15
 * <p>
 * 标记为需要数据源拦截的aop切面，进行动态数据源切换
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DataSourceSwitch {

    /**
     * @return 数据源 字段 名，不设置默认为方法第一个参数
     */
    String value() default "";
}