package com.xxxx.crm.controller;

import com.xxxx.crm.annotation.RequirePermission;
import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.SaleChanceSeivice;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.CookieUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {
    @Resource
    private SaleChanceSeivice saleChanceSeivice;

    @GetMapping("list")
    @ResponseBody
    public Map<String, Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery ,Integer flag,HttpServletRequest request) {
        //判断Lfag参数是否存在区分客户开发计划和营销机会的页面
        if (flag!=null){
            int id = LoginUserUtil.releaseUserIdFromCookie(request);
            saleChanceQuery.setAssignMan(id);
        }
        return saleChanceSeivice.queryByParams(saleChanceQuery);
    }

    //删除营销数据
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteSaleChance(Integer[] ids){
        //删除营销机会的数据
        saleChanceSeivice.deleteSaleChance(ids);
        return success("选中获得营销机会成功删除");
    }

    @RequestMapping("index")
    public String index() {
        return "saleChance/sale_chance";
    }
        //打开营销机会修改/添加的页面
    @RequestMapping("toAddUpdatePage")
    public String toAddUpdatePage(Integer id,HttpServletRequest request){
        if (id!=null){
            SaleChance saleChance = saleChanceSeivice.selectByPrimaryKey(id);
            AssertUtil.isTrue(saleChance==null,"数据异常,请重试一下");
            request.setAttribute("saleChance",saleChance);
        }
        return "saleChance/add_update" ;
    }

    //添加数据
    @RequestMapping("save")
    @ResponseBody
    @RequirePermission(code="101002")
    public ResultInfo save(HttpServletRequest request, SaleChance saleChance) {
        //获取创建人
        String userName = CookieUtil.getCookieValue(request, "userName");
        saleChance.setCreateMan(userName);
        saleChanceSeivice.saveSaleChance(saleChance);
        return success();

    }
    //修改
    @PostMapping("update")
    @ResponseBody
    public ResultInfo update(SaleChance saleChance){
        saleChanceSeivice.updateSaleChance(saleChance);
        return success();
    }
    //查询销售人员
    @PostMapping("queryAllSales")
    @ResponseBody
    public List<Map<String,Object>> queryAllSales(){
        return saleChanceSeivice.queryAllSales();
    }

    @PostMapping("updateDevResult")
    public ResultInfo updateDevResult(Integer id , Integer devResult){
        saleChanceSeivice.updateDevResult(id,devResult);
        return success();
    }

}
