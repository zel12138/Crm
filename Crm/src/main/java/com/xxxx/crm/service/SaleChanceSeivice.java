package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.SaleChanceMapper;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Service
public class SaleChanceSeivice extends BaseService<SaleChance, Integer> {


    @Resource
    private SaleChanceMapper saleChanceMapper;
    //多条件查询数据
    public Map<String, Object> queryByParams(SaleChanceQuery saleChanceQuery){
        Map<String, Object> map = new HashMap<>();
        //开启分页
        PageHelper.startPage(saleChanceQuery.getPage(),saleChanceQuery.getLimit());
        List<SaleChance> saleChances = saleChanceMapper.selectByParams(saleChanceQuery);
        //按照分页条件，格式化数据
        PageInfo<SaleChance> saleChancePageInfo = new PageInfo<>(saleChances);

        map.put("code",0);
        map.put("msg","");
        map.put("count",saleChancePageInfo.getTotal());
        map.put("data",saleChancePageInfo.getList());
        return map;
    }
    //查询所有销售人员
    public List<Map<String ,Object>> queryAllSales(){
        return saleChanceMapper.queryAllSales();
    }
    //删除营销机会
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSaleChance(Integer[] ids){
        //判断要删除的id是否为空
        AssertUtil.isTrue(null==ids||ids.length==0,"请输入要删除的数据！");
        //删除数据
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids)<0,"营销机会数据删除失败");
    }




    @Transactional(propagation = Propagation.REQUIRED)
    public void saveSaleChance(SaleChance saleChance) {

        // 1.参数校验
        checkParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());
        // 2.设置相关参数默认值
        // 未选择分配人
        saleChance.setIsValid(1);
        saleChance.setUpdateDate(new Date());
        saleChance.setCreateDate(new Date());
        // 选择分配人
        if (StringUtils.isNotBlank(saleChance.getAssignMan())) {
            saleChance.setState(0);
            saleChance.setDevResult(0);
        }
        saleChance.setAssignTime(new Date());
        saleChance.setState(1);
        saleChance.setDevResult(1);
            // 3.执行添加 判断结果
        AssertUtil.isTrue(saleChanceMapper.insertSelective(saleChance) < 1, "营销机会数据添加失败！");
    }
    /**
     * 基本参数校验
     * @param customerName
     * @param linkMan
     * @param linkPhone
     */
    private void checkParams(String customerName, String linkMan, String linkPhone)
    {
        AssertUtil.isTrue(StringUtils.isBlank(customerName), "请输入客户名！");
        AssertUtil.isTrue(StringUtils.isBlank(linkMan), "请输入联系人！");
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone), "请输入手机号！");
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone),"手机号格式不正确！");
    }

        //查询所有销售人员
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChance (SaleChance saleChance) {
        // 1.参数校验
        AssertUtil.isTrue(saleChance.getId() == null, "数据异常，请重试");//校验非空参数
        checkParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());//设置默认值
        saleChance.setUpdateDate(new Date());
//通过现有的id查询修改之前的数据
        SaleChance dbSaleChance = saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        AssertUtil.isTrue(dbSaleChance == null, "数据异常，请重试");
//判断原有数据中是否有分配人
        if (StringUtils.isBlank(dbSaleChance.getAssignMan())) {
            //进入当前判断说明修改前没有分配人

            //判断修改后是否有分配人
            if (!StringUtils.isBlank(saleChance.getAssignMan())) {
                //修改后有分配人
                saleChance.setAssignTime(new Date());
                saleChance.setState(1);
                saleChance.setDevResult(1);
            }
            //修改后没有分配人，什么都不做

        } else {
            //进入当前判断说明修改前有分配人

            //判断修改后是否有分配人
            if (StringUtils.isBlank(saleChance.getAssignMan())) {
                //修改后没有分配人
                saleChance.setAssignTime(null);
                saleChance.setState(0);
                saleChance.setDevResult(0);
            } else {
                //修改后有分配人

                //判断前后的分配人是否有变化
                if (!dbSaleChance.getAssignMan().equals(saleChance.getAssignMan())) {
                    //不是一个人，有变化
                    saleChance.setAssignTime(new Date());
                } else {
                    //相同的分配人  那么前台后台都没有设置分配的时间，那么结合刚修改的sql条件，那么原有的数据机会被更改
                    saleChance.setAssignTime(new Date());
                }
            }
        }
        //执行修改操作
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance) < 1,"营销数据修改失败");
    }

    /*
    * 更新开发状态
    * */
    public void updateDevResult(Integer id , Integer devResult){
        SaleChance saleChance = saleChanceMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(  id == null || null == saleChance, "更新数据不存在");
        AssertUtil.isTrue(  null == devResult,"更新状态状态不存在");
            //设置状态
        saleChance.setDevResult(devResult);
        saleChance.setUpdateDate(new Date());
        AssertUtil.isTrue( saleChanceMapper.updateByPrimaryKeySelective(saleChance)< 1,"状态更新失败");
    }

}