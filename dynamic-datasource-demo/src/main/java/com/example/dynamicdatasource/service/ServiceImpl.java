package com.example.dynamicdatasource.service;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.example.dynamicdatasource.entity.User;
import com.example.dynamicdatasource.mappper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : MrLawrenc
 * @date : 2020/5/16 0:35
 * @description : 业务类
 */
@Service
public class ServiceImpl {
    @Autowired
    private UserMapper userMapper;

    /**
     * 注解和手动模式均可以开启
     */
    // @DS("lmy")
    public List<User> lmyList() {
        //可以通过aop的方式实现数据源切换
        DynamicDataSourceContextHolder.push("lmy");
        List<User> users = userMapper.selectList(null);
        System.out.println(users);
        DynamicDataSourceContextHolder.clear();
        return users;
    }

    //    @DS("master")
    public List<User>  masterList() {
        DynamicDataSourceContextHolder.push("master");
        List<User> users = userMapper.selectList(null);
        System.out.println(users);
        DynamicDataSourceContextHolder.clear();
        return users;
    }

    // @DS("lmy")
    public void lmyAdd() {
        DynamicDataSourceContextHolder.push("lmy");
        userMapper.insert(new User(10, "我是插入的lmy1", "", "", "", false, LocalDateTime.now(),LocalDateTime.now()));
        DynamicDataSourceContextHolder.clear();
    }

    //    @DS("master")
    public void masterAdd() {
        DynamicDataSourceContextHolder.push("master");
        userMapper.insert(new User(10, "我是插入的master", "", "", "", false,LocalDateTime.now(),LocalDateTime.now()));
        DynamicDataSourceContextHolder.clear();
    }

}