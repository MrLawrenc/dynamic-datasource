package com.swust.dynamicdatabasemigration.config;

import com.swust.dynamicdatabasemigration.filter.FilterChain;
import com.swust.dynamicdatabasemigration.filter.Request;
import com.swust.dynamicdatabasemigration.filter.standard.Invoker;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.cglib.core.NamingPolicy;
import org.springframework.cglib.core.Predicate;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Random;

/**
 * @author : hz20035009-逍遥
 * @date : 2020/5/14 9:45
 * @description : 为所有{@link Invoker}的实现类生成代理对象，代理对象主要执行过滤器相关逻辑
 * <p>
 * InstantiationAwareBeanPostProcessor代表了Spring的另外一段生命周期：实例化。先区别一下Spring Bean的实例化和初始化两个阶段的主要作用：
 * <p>
 * 1、实例化----实例化的过程是一个创建Bean的过程，即调用Bean的构造函数，单例的Bean放入单例池中
 * <p>
 * 2、初始化----初始化的过程是一个赋值的过程，即调用Bean的setter，设置Bean的属性
 * <p>
 * BeanPostProcessor作用于过程（2）前后，现在的InstantiationAwareBeanPostProcessor则作用于过程（1）前后；
 */
@Component
public class BeanPostProcessorConfig implements InstantiationAwareBeanPostProcessor {
    @SneakyThrows
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        //此处在bean实例化（区分初始化）之前执行
        if (Invoker.class.isAssignableFrom(beanClass)) {
            return proxyInvoker((Class<? extends Invoker>) (beanClass));
        }
        //返回null会执行后续的BeanPostProcessor,只要有任意一个BeanPostProcessor的返回不为null就会停止执行后续的BeanPostProcessor
        return null;
    }

    @Autowired
    private FilterChain filterChain;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    /**
     * 为所有的Invoker对象生成代理对象，在执行{@link Invoker#doInvoke(Request)}方法
     * 前、后进行过滤器的调用
     */
    public <T extends Invoker> T proxyInvoker(Class<T> invoker) {

        Enhancer enhancer = new Enhancer();
        System.out.println("cglib:" + invoker);
        enhancer.setSuperclass(invoker);
        enhancer.setNamingPolicy(new NamingPolicy() {
            @Override
            public String getClassName(String s, String s1, Object o, Predicate predicate) {
                return "Proxy$" + invoker.getSimpleName() + new Random().nextInt(100);
            }
        });
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                return filterChain.buildFilterChain(o, objects, methodProxy).doFilter((Request) objects[0], null);
            }

        });
        return (T) enhancer.create();
    }
}