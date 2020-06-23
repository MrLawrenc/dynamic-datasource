package com.huize.migrationcommon.anno;


import com.huize.migrationcommon.reader.Reader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author MrLawrenc
 * date  2020/6/13 23:17
 * <p>
 * 标记reader writer所属的数据源
 * @see Reader
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DataSourceFlag {

    /**
     * @return 数据源名称
     */
    String datasourceName();

    /**
     * @return 同上
     */
    String value() default "";
}
