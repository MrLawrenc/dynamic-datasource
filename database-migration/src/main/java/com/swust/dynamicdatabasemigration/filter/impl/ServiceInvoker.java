package com.swust.dynamicdatabasemigration.filter.impl;

import com.swust.dynamicdatabasemigration.filter.Request;
import com.swust.dynamicdatabasemigration.filter.Response;
import com.swust.dynamicdatabasemigration.filter.standard.Invoker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author : MrLawrenc
 * @date : 2020/5/13 22:48
 * @description : 具体的业务
 */
@Component
@Slf4j
public class ServiceInvoker implements Invoker {

    @Override
    public Response doInvoke(Request request) {
        System.out.println("service1 doing....................");
        //若使用methodProxy.invokeSuper调用，则当前方法也会被代理，不会存在aop那种同级方法调用代理失效的问题
        //other();
        return null;
    }

    public  void other(){
        System.out.println("invoker1 other method...........");
    }

}