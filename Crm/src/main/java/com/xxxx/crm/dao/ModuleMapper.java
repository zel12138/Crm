package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.model.TreeModel;
import com.xxxx.crm.vo.Module;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ModuleMapper extends BaseMapper<Module,Integer> {
    //同级下名称唯一
     Module queryModulByGradeAName(@Param("grade") Integer grade, @Param("moduleName") String moduleName);

    //查询所以资源
    public List<TreeModel> queryAllModules();
    //查询所有资源
    public List<Module>queryModules();

    Module queryModulByGradeAUrl(@Param("grade")Integer grade, @Param("url")String url);

    Module queryModulByOptValue(String optValue);

    Module queryModulById(Integer parentId);

    Integer deletePermissionByMoudleId(Integer id);


    Integer deleteModuleByMid(Integer mId);
    Integer queryCountModuleByParentId(Integer id);
}