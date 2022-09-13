layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);
        //表单提交

    form.on("submit(login)", function (data){

        console.log(data.field);
        //DOTO数据校验
        //请求转发
        $.ajax({
            type : "post",

            url: ctx + "/user/login" ,
            data:{
            userName :data.field.username,
            userPwd : data.field.password
            },
            dataType: 'json' ,
            success:function (data){
            if(data.code == 200) {
                //记住密码
                if($("#rememberMe").prop( "checked")){
                    $.cookie("userIdStr", data.result.userId);
                    $.cookie("userName", data.result.userName);
                    $.cookie("trueName", data.result.trueName);
                }else {
                    //存储cookie
                $.cookie("userIdStr", data.result.userId);
                $.cookie("userName", data.result.userName);
                $.cookie("trueName", data.result.trueName);

            }

                //跳转到首页
                window.location.href = ctx+"/main";

            }else{
                layer.msg(data.msg,{icon:5});
            }
        }
    });

    return false;


    });

});
