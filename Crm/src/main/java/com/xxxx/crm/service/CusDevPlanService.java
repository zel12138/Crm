package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.CusDevPlanMapper;
import com.xxxx.crm.dao.SaleChanceMapper;
import com.xxxx.crm.query.CusDevPlanQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.CusDevPlan;
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
public class CusDevPlanService  extends BaseService<CusDevPlan,Integer> {
   @Resource
   private CusDevPlanMapper cusDevPlanMapper;
    @Resource
    private SaleChanceMapper saleChanceMapper;

    public Map<String, Object> queryByParams(CusDevPlanQuery query){
        Map<String, Object> map = new HashMap<>();
        //开启分页
        PageHelper.startPage(query.getPage(),query.getLimit());
        List<CusDevPlan> cusDevPlans = cusDevPlanMapper.queryByParams(query);
        //按照分页条件，格式化数据
        PageInfo<CusDevPlan> cusDevPlanPageInfo = new PageInfo<>(cusDevPlans);

        map.put("code",0);
        map.put("msg","");
        map.put("count",cusDevPlanPageInfo.getTotal());
        map.put("data",cusDevPlanPageInfo.getList());
        return map;
    }

    @Transactional
    public void addCusDevPan(CusDevPlan cusDevPlan){
        // 数据校验
        checkParams(cusDevPlan.getSaleChanceId(),cusDevPlan.getPlanItem(),cusDevPlan.getPlanDate());
        //设置默认值
        cusDevPlan.setIsValid(1);
        cusDevPlan.setCreateDate(new Date());
        cusDevPlan.setUpdateDate(new Date());
        //执行添加操作，判断
        AssertUtil.isTrue(cusDevPlanMapper.insertSelective(cusDevPlan) < 1,"计划添加失败");
    }
    private void checkParams(Integer saleChanceId,String planItem,Date planDate) {
        AssertUtil.isTrue(null == saleChanceId ||null == saleChanceMapper.selectByPrimaryKey(saleChanceId), "营销机会数据不存在");
        AssertUtil.isTrue(StringUtils.isBlank(planItem),"计划内容不能为空");
        AssertUtil.isTrue(null == planDate,"计划日期不能为空");
    }


    /**
     * 更新计划项
     * @param cusDevPlan
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCusDevPlan(CusDevPlan cusDevPlan){
        AssertUtil.isTrue(null == cusDevPlan.getId() ||
                        null == selectByPrimaryKey(cusDevPlan.getId()),"待更新记录不存在!");
            // 1. 参数校验
        checkParams(cusDevPlan.getSaleChanceId(),
                cusDevPlan.getPlanItem(),cusDevPlan.getPlanDate());
            // 2. 设置参数默认值
        cusDevPlan.setUpdateDate(new Date());
            // 3. 执行添加，判断结果
        AssertUtil.isTrue(updateByPrimaryKeySelective(cusDevPlan)<1,"计划项记录更新失败!");
    }
    /**
     * 删除计划项
     * @param
     */
    @Transactional
    public void delete(Integer id) {
        CusDevPlan cusDevPlan = cusDevPlanMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(id == null || null == cusDevPlan,"待删除计划项不存在");
        //设置数据失效
        cusDevPlan.setIsValid(0);
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan) < 1,"计划项删除失败");
    }
    /**
     * 更新营销机会的状态
     * 成功 = 2
     * 失败 = 3
     * @param id
     * @param devResult
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChanceDevResult(Integer id, Integer devResult) {
    }

}
