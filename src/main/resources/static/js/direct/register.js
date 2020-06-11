
$(function () {


})


//倒计时
function countDown(num) {
    var obj = $("#yanzhengmaBtn");
    if (num == null) {
        num = 120;
    }
    var timer = setInterval(function () {
        if (num > 1) {
            num--;
            obj.html("重新发送(" + num + ")");
            obj.attr("disabled", "disabled");
            obj.css('background', '#B8B8B8');
        } else {
            obj.text("发送验证码");
            obj.removeAttr("disabled");
            obj.css('background', '#6be659');
            clearInterval(timer);
        }
    }, 1000);

}

//验证手机验证码
// function checkCode(phone,code){
//     $.ajax({
//         url:"http://localhost:8080/user/checkCode",
//         type:"POST",
//         data:{
//             "phone":phone,
//             "code":code
//         },
//         dataType:"json",
//         async:false,
//         success:function(result){
//             var i;
//             var obj=$("#sendVerificationCode");
//             if(obj.prev().is("i")){
//                 obj.prev().remove();
//             }
//             if(result.msg){
//                 i='<i class="layui-icon layui-icon-ok-circle" style="font-size: 25px; color: #009688;"></i>';
//                 $("input[name=code]").attr("disabled", "disabled");
//                 obj.before(i);
//                 obj.remove();
//                 $("input[name=token]").val(result.token);
//             }else{
//                 i='<i class="layui-icon layui-icon-close-fill" style="font-size: 30px; color: red;"></i>';
//                 obj.before(i);
//             }
//         },
//         error: function(request) {
//             alert("Connection error");
//         }
//     })
// }


function checkCode(phone, code) {
    $.ajax({
        url: "http://localhost:8080/user/checkCode",
        type: "POST",
        data: {
            "phone": phone,
            "code": code
        },
        dataType: "json",
        async: false,
        success: function (result) {
            if (result.code != '0001') {
                layer.msg(result.content, {icon: 5, time: 1000,shift : 6})
                return false;
            }
            $("input[name=token]").val(result.content);
            return true;
        },
        error: function (request) {
            alert("Connection error");
        }
    })
}

function login() {
    $.ajax({
        url: "http://localhost:8080/logoreg",
        type: "POST",
        data: {
            "username": $("#loginNums").val(),
            "password": $("#loginForm input[name=password]").val(),
            "remberme": $("#remberBtn").prop("checked")
        },
        dataType: "json",
        async: false,
        success: function (result) {
            console.log(result)
            if(result.code == '0005'){
                window.location.href = '/'
            }
        },
        error: function (request) {
            console.log(request)
            layer.msg(request.responseJSON.content, {icon: 5, time: 1000,shift : 6})
        }
    })
}