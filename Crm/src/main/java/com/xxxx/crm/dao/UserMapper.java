package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.vo.User;
import org.springframework.cglib.beans.BeanMap;

import java.util.List;

public interface UserMapper  extends BaseMapper <User,Integer>{
       //通过用户名查找
        public User queryUserByName(String name);
      //  //  public  User selectByPrimaryKey(Integer id);

        //多条件分页查询
    public List<User> queryUserByParams(UserQuery query);

    //批量删除
    public Integer deleteUsers(Integer[] ids);

}