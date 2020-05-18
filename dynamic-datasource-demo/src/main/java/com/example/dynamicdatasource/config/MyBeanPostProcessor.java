package com.example.dynamicdatasource.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author : MrLawrenc
 * @date : 2020/5/16 0:55
 * @description : TODO
 */
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DynamicRoutingDataSource){
        }
        return null;
    }
}