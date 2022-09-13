package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission,Integer> {

    //根据角色ID 查询对应的权限个数
    Integer countPermission(Integer roleId);
    //查询当前角色拥有的资源
    Integer deletePermissionByRoleId(Integer roleId);

    // 查询当前角色拥有的权限
    List<Integer> selectPermissionByRid(Integer rId);

     List<String> queryUserHasRolesHasPermissions(Integer userId);

    //根据登录用户的id查询对应的权限
    List<Integer> selectAclvalueByUserId(int id);

    Integer deletePermissionByMoudleId(Integer mId);

    Integer queryCountByMoudleId(Integer mId);
}