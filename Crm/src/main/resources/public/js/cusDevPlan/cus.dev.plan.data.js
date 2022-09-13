layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    /**
     * 计划项数据展示
     */
    var  tableIns = table.render({
        elem: '#cusDevPlanList',
        url : ctx+'/cus_dev_plan/list?sId='+$( '[name="id"]').val(),
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "cusDevPlanListTable",
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',fixed:"true"},
            {field: 'planItem', title: '计划项',align:"center"},
            {field: 'exeAffect', title: '执行效果',align:"center"},
            {field: 'planDate', title: '执行时间',align:"center"},
            {field: 'createDate', title: '创建时间',align:"center"},
            {field: 'updateDate', title: '更新时间',align:"center"},
            {title: '操作',fixed:"right",align:"center", minWidth:150,templet:"#cusDevPlanListBar"}
        ]]
    });


    /**
     * 监听头部工具栏
     */
    table.on('toolbar(cusDevPlans)', function(obj){
        var checkStatus = table.checkStatus(obj.config.id);
        console.log(checkStatus.data);
        switch(obj.event){
            case "add":
                openAddOrUpdateCusDevPlanDialog();
                break;
            case 'success':
                updateDevResult(2);
                break;
            case 'failed':
                updateDevResult(3);
                break;
        };
    });
    /**
     * 打开计划项数据页面
     */
    function openAddOrUpdateCusDevPlanDialog(id){
        var title = "<h2>计划项管理-添加计划项</h2>";
        var url = ctx+"/cus_dev_plan/toAddOrUpdatePage?sId="+$('[name="id"]').val();
        if(id){
            title = "计划项管理-更新计划项";
            url += "&id="+id;
        }
        layui.layer.open({
            title : title,
            type : 2,
            area:["500px","300px"],
            maxmin:true,
            content : url
        });
    }
    function updateDevResult(devResult){
        var id=$('[name=id]').val();
        $.post(ctx+"/sale_chance/updateDevResult",{id:id,devResult:devResult},function (data){
            if(data.code == 200){
                layer.close(index);
                // 重新加载表格
                parent.location.reload();
            }else{
                layer.msg(data.msg,{icon:5});
            }
        })


    }
    /**
     * 行监听工具栏
     */
    table.on("tool(cusDevPlans)", function(obj){

        // 监听编辑事件
        if(obj.event== "edit") {
            openAddOrUpdateCusDevPlanDialog(obj.data.id);
        }else if(obj.event == "del"){
            // 询问用户是否确认删除
            deleteCusDevPlan(obj.data.id);

        }
    });
    function deleteCusDevPlan(id){
        layer.confirm("确定删除当前数据?", {icon:3, title:"开发计划管理"}, function (index) {
            $.post(ctx+"/cus_dev_plan/delete",{id:id},function (data){
                if(data.code == 200){
                    layer.msg("操作成功!");
                    // 重新加载表格
                    tableIns.reload();
                }else{
                    layer.msg(data.msg,{icon:5});
                }
            });


        });
    }


});
