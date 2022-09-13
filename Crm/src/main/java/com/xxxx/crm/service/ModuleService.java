package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.dao.RoleMapper;
import com.xxxx.crm.model.TreeModel;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Module;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module,Integer> {
    @Resource
    private ModuleMapper moduleMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private PermissionMapper permissionMapper;

    //查询所以资源
    public List<TreeModel> queryAllModules(Integer rId) {
        //角色非空且存在
        AssertUtil.isTrue(rId == null || roleMapper.selectByPrimaryKey(rId) == null, "角色不存在");
        //查询当前角色拥有的权限
        List<Integer> mIds = permissionMapper.selectPermissionByRid(rId);
        //查询所有的模块
        List<TreeModel> treeModels = moduleMapper.queryAllModules();
        //遍历需要返回到前台的所有资源
        for (TreeModel treeModel : treeModels) {
            //获取当前遍历对象的模块id
            Integer id = treeModel.getId();
            //判断当前角色拥有的权限中是否包含了 遍历对象的模块id
            if (mIds.contains(id)) {  //当前方法判断某个数据是否存在于这个集合中
                treeModel.setChecked(true);
                treeModel.setOpen(true);
            }
        }
        return treeModels;
    }

    //查询所有资源资源管理使用
    public Map<String, Object> queryModules() {
        Map<String, Object> map = new HashMap<>();
        List<Module> modules = moduleMapper.queryModules();
        AssertUtil.isTrue(modules==null||modules.size()<1,"资源数据异常");
        //准备前台需要的数据接口
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", modules.size());
        map.put("data", modules);
        return map;

    }
    /*
    *
    *
        * 模块添加
        *   1数据校验
        *       模块名称非空，同级唯一
        *  地址URL
        *       二级菜单:非空,同级唯一
           父级菜单parentId
                一级:null/-1二级|三级:非空八
        * 层级grade
            非空值必须为0/1/2
         权限码
            非空瞭唯一
        2。默认值
        is_vaLid
        * updateDate
        * createDate
        * 3.执行添加操作


    */
    @Transactional
    public void moduleAdd(Module module) {
        //模块名称， 非空
        AssertUtil.isTrue(module.getGrade()==null,"层级不能为空");
        AssertUtil.isTrue(!(module.getGrade()==0||module.getGrade()==1||module.getGrade()==3),"层级有误");


        //模块名称 非空  同级唯一
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"模块名称不能为空");
        Module dbModule=moduleMapper.queryModulByGradeAName(module.getGrade(),module.getModuleName());
        AssertUtil.isTrue(dbModule!=null,"模块名称不存在");

        //二级菜单URL:非空，同级唯一
        if(module.getGrade() == 1) {
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()), "模块地址不能为空");
            dbModule = moduleMapper.queryModulByGradeAUrl(module.getGrade(), module.getUrl());
            AssertUtil.isTrue(dbModule == null, "地址已存在，请重新输入");
        }
        //父级菜单  二级| 三级 非空  必须存在
        if(module.getGrade() == 2||module.getGrade() == 1) {
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()), "夫ID不为空");
            dbModule = moduleMapper.queryModulById(module.getParentId());
            AssertUtil.isTrue(dbModule == null, "父ID不存在");
        }
            //权限码 非空 唯一
            AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"父ID不能为空");
            dbModule=moduleMapper.queryModulByOptValue(module.getOptValue());
            AssertUtil.isTrue(dbModule!=null,"权限码已存在");

            //设置默认值
            module.setIsValid((byte) 1 );
            module.setCreateDate(new Date());
            module.setUpdateDate(new Date());
            //执行添加操作判断受影响行数
            AssertUtil.isTrue(  moduleMapper.insertSelective(module) < 1,"模块添加失败");

    }
    /**
     * 修改模块
     1.数据校验
     id
     非空，并且资源存在
     模块名称
     非空，同级唯一
     地址 URL
     二级菜单：非空，同级唯一
     父级菜单 parentId
     一级：null | -1
     二级|三级：非空 | 必须存在
     层级 grade
     非空  值必须为 0|1|2
     权限码
     非空  唯一
     2.默认值
     is_valid
     updateDate
     createDate
     3.执行修改操作  判断受影响行数
     * @param module
     */
    @Transactional
    public void moduleUpdate(Module module) {
        // id  非空，并且资源存在
        AssertUtil.isTrue(module.getId() == null,"待删除的资源不存在");
        Module dbModule = moduleMapper.selectByPrimaryKey(module.getId());
        AssertUtil.isTrue(dbModule == null,"系统异常");

        //层级 grade  非空  值必须为 0|1|2
        AssertUtil.isTrue(module.getGrade() == null,"层级不能为空");
        AssertUtil.isTrue(!(module.getGrade() == 0 || module.getGrade() == 1 || module.getGrade() == 2),"层级有误");

        //模块名称 非空  同级唯一
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"模块名称不能为空");
        dbModule = moduleMapper.queryModulByGradeAName(module.getGrade(),module.getModuleName());
        AssertUtil.isTrue(dbModule != null && !(module.getId().equals(dbModule.getId())),"模块名称已存在");

        // 二级菜单URL：非空，同级唯一
        if(module.getGrade() == 1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"模块地址不能为空");
            dbModule = moduleMapper.queryModulByGradeAUrl(module.getGrade(),module.getUrl());
            AssertUtil.isTrue(dbModule != null && !(module.getId().equals(dbModule.getId())),"地址已存在，请重新输入");
        }

        //父级菜单  二级|三级：非空 | 必须存在
        if(module.getGrade() == 1 || module.getGrade() == 2){
            AssertUtil.isTrue(module.getParentId() == null,"父ID不能为空");
            dbModule = moduleMapper.queryModulById(module.getParentId());
            AssertUtil.isTrue(dbModule == null && !(module.getId().equals(dbModule.getId())),"父ID不存在");
        }

        //权限码  非空  唯一
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"权限码不能为空");
        dbModule = moduleMapper.queryModulByOptValue(module.getOptValue());
        AssertUtil.isTrue(dbModule != null && !(module.getId().equals(dbModule.getId())),"权限码已存在");

        //默认值
        module.setUpdateDate(new Date());

        //执行修改操作
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(module) < 1,"资源修改失败");
    }
    /**
     * 逻辑删除资源
     *      逻辑判断
     *          1.参数id 非空，删除的数据必须存在
     *          2.查询当前id下是否有子模块，如果有不能删除
     *          3.查询权限表中(角色和资源)是否包含当前模块的数据，有则删除
     *          4.删除资源
     * @param
     */
    public void deletePermissionByMoudleId(Integer mId) {
        //参数id 非空，删除的数据必须存在
        AssertUtil.isTrue(mId == null,"系统异常，请重试");
        AssertUtil.isTrue(selectByPrimaryKey(mId) == null,"待删除数据不存在");

        //查询当前id下是否有子模块，如果有不能删除
        Integer count = moduleMapper.queryCountModuleByParentId(mId);
        AssertUtil.isTrue(count > 0,"该模块下存在子模块，不能删除");

        //查询权限表中(角色和资源)是否包含当前模块的数据，有则删除
        count = permissionMapper.queryCountByMoudleId(mId);
        if(count > 0){
            AssertUtil.isTrue(permissionMapper.deletePermissionByMoudleId(mId) != count ,"权限删除失败");
        }

        //删除资源
        AssertUtil.isTrue(moduleMapper.deleteModuleByMid(mId) < 1,"资源删除失败");
    }


}