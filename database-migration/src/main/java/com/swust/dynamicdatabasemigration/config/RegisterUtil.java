package com.swust.dynamicdatabasemigration.config;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author : MrLawrenc
 * @date : 2020/5/13 23:33
 * @description : TODO
 */
public class RegisterUtil implements ImportBeanDefinitionRegistrar {
    public static BeanDefinitionRegistry registry;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        RegisterUtil.registry = registry;
    }
}