package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.query.RoleQuery;

import com.xxxx.crm.vo.Role;

import java.util.List;
import java.util.Map;

public interface RoleMapper extends BaseMapper<Role,Integer> {


    //<!--查询对应的角色名称和id反馈给前台使用-->
    public List<Map<String,Object>> queryAllRoles(Integer id);

    /*//查询所以人
    public List<Map<String,Object>> queryAll(RoleQuery roleQuery);*/
    // <!--查询对应的角色名称和id反馈给前台使用-->

    /*通过名称查询角色数据*/
    public Role queryRoleByRoleName(String roleName);

    public void deteteRole(Integer id);
}