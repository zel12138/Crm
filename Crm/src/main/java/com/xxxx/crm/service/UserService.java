package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.dao.UserRoleMapper;
import com.xxxx.crm.query.UserModel;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.Md5Util;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.utils.UserIDBase64;
import com.xxxx.crm.vo.SaleChance;
import com.xxxx.crm.vo.User;
import com.xxxx.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserService  extends BaseService<User,Integer> {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;

    public ResultInfo loginCheck(String userName, String userPwd) {


        //校验参数是否为空
        checkLogData(userName, userPwd);

        //掉用dao层查询通过用户名查询数据库数据
        User user = userMapper.queryUserByName(userName);

        //判断账号是否存在
        AssertUtil.isTrue(user == null, "账号不存在");

        //校验前台传来的密码和数据库中的密码是否一致〔前台密码加密后再校验)
        checkLoginPwd(user.getUserPwd(), userPwd);

        //装ResultInfo对象给前台（根据前台需求:usermodel对象封装后传到前台使用)

        ResultInfo resultInfo = buildResultInfo(user);


        return resultInfo;


    }

    public void userUpdate(Integer userId, String oldPassword, String newPassword, String confirmPassword) {
        //确保登陆状态
        AssertUtil.isTrue(userId == null, "用户未登陆");
        User user = userMapper.selectByPrimaryKey(userId);
        AssertUtil.isTrue(user == null, "用户状态异常");
        //校验密码数据
        checkUpdateData(oldPassword, newPassword, confirmPassword, user.getUserPwd());
        //执行修改操作，返回ResultInfo
        user.setUserPwd(Md5Util.encode(newPassword));
        user.setUpdateDate(new Date());
        //判断是否修改成功
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1, "密码修改失败");


    }

    /*
     * 密码校验*/
    private void checkUpdateData(String oldPassword, String newPassword, String confirmPassword, String dbPwd) {
        //验证输入的老密码非空且与数据库密码是一样
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword), "原始密码不存在");
        AssertUtil.isTrue(!dbPwd.equals(Md5Util.encode(oldPassword)), "原始密码不存在");


        //输入的新密码不能为空且不能与数据库的老密码一样
        AssertUtil.isTrue(StringUtils.isBlank(newPassword), "新密码不能为空");
        AssertUtil.isTrue(oldPassword.equals(newPassword), "新密码不能和原密码一致");


        //确认密码非空确认必须和新密码一致
        AssertUtil.isTrue(StringUtils.isBlank(confirmPassword), "确认密码不能为空");
        AssertUtil.isTrue(!confirmPassword.equals(newPassword), "确认必须和新密码一致");


    }


    //准备前台需要的数据
    private ResultInfo buildResultInfo(User user) {
        ResultInfo resultInfo = new ResultInfo();

        //封装userMdel cookie需要的数据
        UserModel userModel = new UserModel();
        //加密userid
        String id = UserIDBase64.encoderUserID(user.getId());
        userModel.setUserId(id);
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());

        resultInfo.setResult(userModel);
        return resultInfo;

    }

    private void checkLoginPwd(String dbPwd, String userPwd) {
        //将传来的密码加密再校验
        String encodePwd = Md5Util.encode(userPwd);
        //校验
        AssertUtil.isTrue(!encodePwd.equals(dbPwd), "用户密码错误");
    }


    /*
     * 非空用户登陆校验
     * */
    private void checkLogData(String userName, String userpwd) {
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户名不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(userpwd), "用户密码不能为空");

    }


    /*
     * 多条件分页查询
     * */
    public Map<String, Object> queryUserByParams(UserQuery query) {
        Map<String, Object> map = new HashMap<>();
        //开启分页
        PageHelper.startPage(query.getPage(), query.getLimit());
        List<User> users = userMapper.queryUserByParams(query);
        //按照分页条件，格式化数据
        PageInfo<User> userPageInfo = new PageInfo<>(users);

        map.put("code", 0);
        map.put("msg", "");
        map.put("count", userPageInfo.getTotal());
        map.put("data", userPageInfo.getList());
        return map;
    }


    /*添加用户
     *   1.校验参数
     *       用户  不为空| 唯一
     *       密码  非空
     *       手机号     非空|格式正确
     * 2.设置默认值
     *       is_valid
     *        update_date
     *        create_date
     *        user_password  设置用户默认密码123456(md5加密）
     * 3.执行添加操作
     *
     * */
    public void saveUser(User user) {
        AssertUtil.isTrue(StringUtils.isBlank(user.getUserName()), "用户名称不能为空");
        AssertUtil.isTrue(null != userMapper.queryUserByName(user.getUserName()), "用户名已存在");
        checkUserParams(user.getEmail(),user.getPhone());
        //设置默认值
        user.setUpdateDate(new Date());
        user.setIsValid(1);
        user.setCreateDate(new Date());
        //设置用户默认密码
        user.setUserPwd(Md5Util.encode("123456"));
        //执行添加操作
       // AssertUtil.isTrue(userMapper.insertSelective(user)<1,"添加用户失败");
        //主键返回到user对象中
        AssertUtil.isTrue(userMapper.insertHasKey(user)<1,"添加用户失败");
        relationUserRole(user.getId(),user.getRoleIds());

    }
    /*
    * 给用户绑定角色
    *
    * */
    private void relationUserRole(Integer id, String roleIds) {
        //修改角色操作：查询是否原来就有角色，如果有那么直接删除再绑定新角色
        Integer count = userRoleMapper.countUserRole(id);
        if(count > 0){
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUid(id) != count,"原有角色删除失败");
        }
        AssertUtil.isTrue(roleIds == null,"角色不存在");
        //准备一个容器接收遍历出来的新对象/新数据
        List<UserRole> urs = new ArrayList<>();
        //切割获取到每个id
        String[] splits = roleIds.split(",");
        for(String idStr:splits){
            UserRole userRole = new UserRole();
            userRole.setUserId(id);
            userRole.setRoleId(Integer.parseInt(idStr));
            userRole.setUpdateDate(new Date());
            userRole.setCreateDate(new Date());

            //将数据添加到集合中
            urs.add(userRole);
        }
        //执行批量添加操作
        AssertUtil.isTrue(userRoleMapper.insertBatch(urs) != splits.length,"角色绑定失败");

    }

    /*
    *
    * 修改用户1.校验参数
        id
        非空|存在
        用户名
        非空|唯一"工
        邮箱
        非空
        手机号非空│格式正确2.设置默认值
        update_date3.执行修改操作

    * */
    public void updateUser(User user){
        //id     非空|存在
        AssertUtil.isTrue(null == user.getId() || null == userMapper.selectByPrimaryKey(user.getId()),"数据异常请重试");
        //用户名  非空 | 唯一
        AssertUtil.isTrue(user.getUserName() == null,"用户名不能为空");
        // 名称唯一
        User dbUser = userMapper.queryUserByName(user.getUserName());
        AssertUtil.isTrue(dbUser != null && user.getId() != dbUser.getId(),"用户名已存在");
        //校验邮箱和手机号
        checkUserParams(user.getEmail(),user.getPhone());
        //设置默认值
        user.setUpdateDate(new Date());
        //执行修改操作
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1,"用户修改失败");
        //修改绑定的角色
        relationUserRole(user.getId(),user.getRoleIds());
    }


    private void checkUserParams( String email, String phone) {
        AssertUtil.isTrue(StringUtils.isBlank(email), "邮箱不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(phone), "手机号不能为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone), "手机号格式不正确");

    }
    /*
    * 批量删除
    * */


    public void deleteUsers(Integer[] ids){
        AssertUtil.isTrue(ids==null||ids.length<1,"未选中删除数据");
        AssertUtil.isTrue(userMapper.deleteUsers(ids)!= ids.length,"用户删除失败");
    }




}