package com.swust.dynamicdatabasemigration;

import com.github.mrLawrenc.filter.entity.Request;
import com.github.mrLawrenc.filter.entity.Response;
import com.github.mrLawrenc.filter.standard.Invoker;
import org.springframework.stereotype.Component;

/**
 * @author : hz20035009-逍遥
 * @date : 2020/5/19 15:09
 * @description : TODO
 */
@Component
public class ServiceInvoker implements Invoker {
    @Override
    public Response doInvoke(Request request) {
        System.out.println("do invoke ....");
        return new Response();
    }
}