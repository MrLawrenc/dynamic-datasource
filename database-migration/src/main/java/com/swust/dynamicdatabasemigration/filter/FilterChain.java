package com.swust.dynamicdatabasemigration.filter;

import com.alibaba.fastjson.JSON;
import com.swust.dynamicdatabasemigration.config.RegisterUtil;
import com.swust.dynamicdatabasemigration.filter.impl.FirstFilter;
import com.swust.dynamicdatabasemigration.filter.impl.LastFilter;
import com.swust.dynamicdatabasemigration.filter.standard.Filter;
import com.swust.dynamicdatabasemigration.filter.standard.InboundFilter;
import com.swust.dynamicdatabasemigration.filter.standard.Invoker;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * @author : MrLawrenc
 * @date : 2020/5/13 22:37
 * @description : 过滤器链
 * <p>
 * first和last filter是一定会被执行的
 */
@Component
@Slf4j
public class FilterChain implements InitializingBean {

    @Autowired
    private ApplicationContext context;

    @Value("${filter.basepkg: }")
    private String basePkg;

    /**
     * 所有非first和last过滤器
     */
    private List<Filter> beanFilters;


    private List<FirstFilter> firstFilters;
    private List<LastFilter> lastFilters;
    /**
     * 当前执行了几个过滤器
     */
    private int current;

    /**
     * 当前执行业务方法的对象
     */
    private Invoker currentInvoker;


    /**
     * 是否为拷贝额副本
     */
    private boolean copy = false;
    private Object proxyObj;
    private Object[] args;
    private MethodProxy methodProxy;


    public FilterChain buildFilterChain(Object proxyObj, Object[] args, MethodProxy methodProxy) {
        this.proxyObj = proxyObj;
        this.args = args;
        this.methodProxy = methodProxy;
        //通过代理类调用父类中的方法
        //Object result = methodProxy.invokeSuper(o, objects);
        return this;
    }

    public Response doFilter(Request request, Response response) {
        current = 0;
        if (current < beanFilters.size()) {
            Filter filter = beanFilters.get(current++);
            if (filter instanceof InboundFilter) {
                filter.doFilter(request, response, this);
            }

            this.doFilter(request, response);
        }
        return currentInvoker.doInvoke(request);
    }

    public Filter pre() {
        return beanFilters.get(--current);
    }

    /**
     * 初始化当前环境中所有filter
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        basePkg = basePkg.strip();
        beanFilters = new ArrayList<>(context.getBeansOfType(Filter.class).values());

        if (!StringUtils.isEmpty(basePkg)) {
            System.out.println("扫描包:" + basePkg);
            Reflections reflections = new Reflections(basePkg);
            Set<Class<? extends Filter>> subClz = reflections.getSubTypesOf(Filter.class);

            List<? extends Class<? extends Filter>> sourceClz = beanFilters.stream().map(Filter::getClass).collect(toList());

            List<Class<? extends Filter>> newBeanFilter = subClz.stream().filter(c -> !sourceClz.contains(c)).peek(result -> {
                RootBeanDefinition definition = new RootBeanDefinition();
                definition.setBeanClass(result);
                RegisterUtil.registry.registerBeanDefinition(result.getSimpleName().toLowerCase(), definition);
                beanFilters.add(context.getBean(result));
            }).collect(toList());

            log.info("add bean : {}", newBeanFilter);
        }

        firstFilters = beanFilters.stream().filter(f -> f instanceof FirstFilter)
                .map(f -> (FirstFilter) f).collect(toList());
        lastFilters = beanFilters.stream().filter(f -> f instanceof LastFilter)
                .map(f -> (LastFilter) f).collect(toList());
        beanFilters.removeAll(firstFilters);
        beanFilters.removeAll(lastFilters);

        //排序
        AnnotationAwareOrderComparator.sort(beanFilters);

        //first全部添加到入站头，last添加到出站头
        AnnotationAwareOrderComparator.sort(firstFilters);
        AnnotationAwareOrderComparator.sort(lastFilters);
    }


    /**
     * 一般在用户自定义的{@link FirstFilter#doFilter(Request, Response, FilterChain)}被调用
     * <p>
     * 允许用户在特殊条件更改本次执行的过滤器链，更改只会在本次请求生效，如果想要复用，就不能调用{@link FilterChain#clearCopyChain()}方法，
     * 并且需要保存当前FilterChain副本，以便下次在进入{@link FirstFilter#doFilter(Request, Response, FilterChain)}方法时更改当前的filterChain
     * <p>
     * <p>
     * 重新装配filter,不会修改原有的first和last类型的过滤器。
     *
     * @param filterList 新的过滤器链
     */
    public FilterChain reloadFilters(List<Filter> filterList) {
        FilterChain filterChain = JSON.parseObject(JSON.toJSONString(this), FilterChain.class);
        filterChain.beanFilters = filterList;
        filterChain.copy = true;
        filterChain.current = 0;
        return filterChain;
    }

    /**
     * 清理过滤器链副本数据,通常在用户自定义的{@link LastFilter#doFilter(Request, Response, FilterChain)}执行
     */
    public void clearCopyChain() {
        if (this.copy) {
            this.beanFilters.clear();
            this.firstFilters.clear();
            this.lastFilters.clear();
        }
    }
}