package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.exceptions.ParamsException;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController  extends BaseController {
    @Resource
    private UserService userService;

    @PostMapping("login")
    @ResponseBody
    public ResultInfo login(String userName,String userPwd){
        return userService.loginCheck(userName,userPwd);

    }
    //修改密码
    @PostMapping("update")
    @ResponseBody
    public ResultInfo update(HttpServletRequest request, String oldPassword, String newPassword, String confirmPassword){
        ResultInfo resultInfo= new ResultInfo();
        //获取用户id
        int id = LoginUserUtil.releaseUserIdFromCookie(request);
        userService.userUpdate(id, oldPassword,newPassword,confirmPassword);
        return success();
    }
    //打开修改密码页面
    @RequestMapping("toPasswordPage")
    public String toPasswordPage(){

        return "user/password";
    }
    //多分页查询
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryUserByParams(UserQuery query){
        return userService.queryUserByParams(query);
    }


    @RequestMapping("index")
    public String index(){
        return "user/user";
    }

    @PostMapping( "save" )@ResponseBody
    public ResultInfo saveUser(User user){
        userService.saveUser(user);
        return success( "用户添加成功");
    }

    @PostMapping ( "updateUser" )
    @ResponseBody
    public ResultInfo updateUser(User user){
        userService.updateUser(user);
        return success("用户修改成功");
    }

    //打开添加/修改页面
    @RequestMapping( "toUpdateAddPage")
    public String toUpdateAddPage(Integer id,HttpServletRequest request){
        if(id != null){
            User user = userService.selectByPrimaryKey(id);
            AssertUtil.isTrue(user == null, "数据异常请重试");
            request.setAttribute( "user" , user);
        }

        return "user/add_update" ;
    }


    //批量删除用户
    @PostMapping("deleteBatch")
    @ResponseBody
    public ResultInfo deleteUsers(Integer[] ids){
        userService.deleteUsers(ids);
        return success();

    }



}
