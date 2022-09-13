package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.vo.SaleChance;

import java.util.List;
import java.util.Map;

public interface SaleChanceMapper extends BaseMapper<SaleChance,Integer> {


    int updateByPrimaryKey(SaleChance record);
    //多条件分页查询数据
    public List<SaleChance> selectByParams(SaleChanceQuery query);

    //查询所有销售人员数据
    public List<Map<String,Object>> queryAllSales();

    /*更新开发状*/
public Integer updateDevResult(Integer id,Integer devResult);

}