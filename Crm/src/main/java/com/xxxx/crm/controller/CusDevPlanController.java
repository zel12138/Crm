package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.CusDevPlanQuery;
import com.xxxx.crm.service.CusDevPlanService;
import com.xxxx.crm.service.SaleChanceSeivice;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.CusDevPlan;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("cus_dev_plan")
public class CusDevPlanController extends BaseController {
      @Resource
    private SaleChanceSeivice saleChanceSeivice;

    @Resource
    private CusDevPlanService cusDevPlanService;

    @RequestMapping("index")
    public String toIndex(){
        return "cusDevPlan/cus_dev_plan";
    }

    /**
     * 进入开发计划项数据页面
     * @param model
     * @param sid
     * @return
     */
    @RequestMapping("toCusDevPlanDataPage")
    public String toCusDevPlanDataPage (Model model, Integer sid) {
        // 通过id查询营销机会数据
        SaleChance saleChance = saleChanceSeivice.selectByPrimaryKey(sid);
        // 将数据存到作用域中
        model.addAttribute("saleChance", saleChance);
        return "cusDevPlan/cus_dev_plan_data";
    }


    /*多条件分页查询客户计划表*/
    @RequestMapping( "list")
    @ResponseBody
    public Map<String,Object> queryByParams(CusDevPlanQuery query){
        return cusDevPlanService.queryByParams(query);
    }
    /**
     * 更新计划项
     * @param cusDevPlan
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.updateCusDevPlan(cusDevPlan);
        return success("计划项更新成功!");
    }

    /*
       *计划项数据的添加*@paramcusDevPlan*areturn
    */
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo addCusDevPan(CusDevPlan cusDevPlan){
        cusDevPlanService.addCusDevPan(cusDevPlan);
        return success( "计划添加成功");
    }

    /**
     * 转发跳转到计划数据项页面
     * @param
     * @param
     * @param
     * @return
     */
    @RequestMapping("toAddOrUpdatePage")
    public String updateCusDevPlanPage(Integer id,Integer sId,HttpServletRequest request){
        if(id != null){
            CusDevPlan cusDevPlan = cusDevPlanService.selectByPrimaryKey(id);
            AssertUtil.isTrue(  null == cusDevPlan,"计划项数据异常请重试");
            request.setAttribute("cusDevPlan",cusDevPlan);
        }
        request.setAttribute("sId",sId);

  /*      //修改操作有idif(id != null){
        CusDevPlan cusDevPlan = cusDevPlanService.selectByPrimaryKey(id);
        AssertUtil.isTrue(  null == cusDevPlan, "计划项数据异常请重试");
        request.setAttribute( "cusDevPlan " , cusDevPlan);*/
        return "cusDevPlan/add_update";
    }
    /**
     * 删除计划项
     * @param //id
     * @return
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo delete(Integer id){
        cusDevPlanService.delete(id);
        return success("计划项删除成功!");
    }


}
