package com.swust.dynamicdatabasemigration.controller.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swust.dynamicdatabasemigration.controller.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : MrLawrenc
 * @date : 2020/5/16 0:36
 * @description : TODO
 */
@Mapper
public interface TableMapper extends  BaseMapper<User>  {


}