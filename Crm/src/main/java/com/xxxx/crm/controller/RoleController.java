package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.RoleQuery;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.service.RoleService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("role")
public class RoleController extends BaseController {
        @Resource
        private RoleService roleService;

    //<!--查询对应的角色名称和id反馈给前台使用-->
    @RequestMapping("queryAllRoles")
    @ResponseBody
    public List<Map<String,Object>> queryAllRoles(Integer id){
        return roleService.queryAllRoles(id);
    }
    @RequestMapping("index")
    public String index() {
        return "role/role";
    }
    //  查询所以人
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryAll(RoleQuery roleQuery){
        return roleService.queryByParamsForTable(roleQuery);
    }
    //  跳转的授权页面
    @RequestMapping("toAddGrantPage")
    public String toAddGrantPage(Integer rId, HttpServletRequest request){
        AssertUtil.isTrue(rId == null,"角色不存在");
        request.setAttribute("roleId",rId);
        return "role/grant";
    }


    //绑定权限
    @PostMapping("addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer roleId,Integer[] mIds){
        roleService.addGrant(roleId,mIds);
        return  success();

    }
    @RequestMapping("addOrUpdateRolePage")
    public String addUserPage(Integer id, Model model){
        if(null !=id){
            model.addAttribute("role",roleService.selectByPrimaryKey(id));
        }
        return "role/add_update";
    }
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveRole(Role role){
        roleService.saveRole(role);
        return success("角色记录添加成功");
    }
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateRole(Role role){
        roleService.updateRole(role);
        return success("角色记录更新成功");
    }
    /**
     * 删除角色
     * @param //id
     * @return
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo delete(Integer id){
        roleService.delete(id);
        return success("角色删除成功!");
    }



}
