package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.model.TreeModel;
import com.xxxx.crm.service.ModuleService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Module;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("module")
public class ModuleController extends BaseController {
    @Resource
    private ModuleService moduleService;

    //查询所以资源
    @RequestMapping("queryAllModules")
    @ResponseBody
    public List<TreeModel> queryAllModules(Integer rId){
        return moduleService.queryAllModules(rId);
    }

    //查询所有资源资源管理使用
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryModules() {
        return moduleService.queryModules();
    }


    //查询所有资源资源管理使用
    @RequestMapping("index")
    public String index() {
        return "module/module";
    }

    @RequestMapping("toAdd")
    public String toAdd(Integer grade, Integer parentId, HttpServletRequest request){
        request.setAttribute(  "grade" ,grade);
        request.setAttribute(  "parentId" ,parentId);
        return "module/add";
    }

    @RequestMapping( "add")@ResponseBody
    public ResultInfo moduleAdd(Module module){
        moduleService.moduleAdd(module);
        return success("资源添加成功");
    }
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo moduleUpdate(Module module){
        moduleService.moduleUpdate(module);
        return success("资源修改成功");
    }

    @RequestMapping( "updateModulePage")
    public String updateModulePage(Integer id,HttpServletRequest request) {
        Module module = moduleService.selectByPrimaryKey(id);
        request.setAttribute("module", module);
        return "module/update";

    }


    @PostMapping("delete")
    @ResponseBody
    public ResultInfo delete(Integer mId){
        moduleService.deletePermissionByMoudleId(mId);
        return success("角色删除成功!");
    }//删除营销数据


}
