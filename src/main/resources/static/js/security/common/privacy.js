$(function () {

})
layui.use('layer',function(){




})

function privacy(){
    $.ajax({
        url: "http://localhost:8080/user/getUserDetail",
        type: "GET",
        dataType: "json",
        async: false,
        success: function (result) {
            console.log(result)
            var user = result.content;
            if(user != null){
                var phone = user.phone;
                var html = '<div>' +
                    '                        <span style="font-size: 20px;font-family: cursive;" id="spanPhone">手机号: '+phone+'</span>' +
                    '                        <button type="button" class="btn btn-danger btn-xs"' +
                    '                                style="margin-left: 4px;margin-top: 0px;padding-top: 0px;" onclick="openPhoneModal()">修改' +
                    '                        </button>' +
                    '                    </div>' +
                    '                    <br>' +
                    '                    <div>' +
                    '                        <span style="font-size: 20px;font-family: cursive;">密码: ********</span>' +
                    '                        <button type="button" class="btn btn-danger btn-xs"' +
                    '                                style="margin-left: 4px;margin-top: 0px;padding-top: 0px;" onclick="openModal()">修改' +
                    '                        </button>' +
                    '                    </div>';
                $("#result").empty();
                $("#result").append(html);
                $("#paging").empty()
                $("#upPhone").val(phone)
            }

        },
        error: function (request) {
            var code = request.responseJSON.code;
            if(code == "0004"){
                layer.confirm('您还未登陆，现在去登陆？', {
                    btn: ['确定','取消'] //按钮
                }, function(){
                    layer.closeAll('dialog');
                    window.location.href="/logoreg";
                });
                return;
            }
            layer.msg(request.responseJSON.msg, {icon: 5, time: 1000,shift : 6})
        }
    })
}



// -----下面赶时间，没有整理-----
function openModal() {
    $('#upPwdModal').modal('show')
}
function upPwdBtn(){
    var phone = $("#upPhone").val();
    var phoneReg = /(^1\d{10}$)|(^[0-9]\d{7}$)/;
    if(phone == ''){
        layer.msg("请填写手机号！", {icon: 5, time: 1000,shift : 6})
        return false;
    }
    if(!phoneReg.test(phone)) {
        layer.msg("手机号码格式错误！", {icon: 5, time: 1000,shift : 6})
        return false;
    }
    var code = $("#upYzm").val();
    if(code == ''){
        layer.msg("请填写验证码！", {icon: 5, time: 1000,shift : 6})
        return false;
    }
    if(code.length != 6){
        layer.msg("验证码错误！", {icon: 5, time: 1000,shift : 6})
        return false;
    }
    var token = $("input[name=upToken]").val();
    var isTrueCode = true
    if(token == ''){
        checkCode2(phone,code);
    }
    token = $("input[name=upToken]").val();
    if(token == ''){
        return false;
    }
    var password = $("#upPwd").val();
    if(password == ''){
        layer.msg("密码不能为空！", {icon: 5, time: 1000,shift : 6})
        return false;
    }
    var length = password.length;
    if(length<8 || length>12){
        layer.msg("密码长度应在8到12位！", {icon: 5, time: 1000,shift : 6})
        return false;
    }
    upPassword(phone,password,token);
}
function checkCode2(phone, code) {
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
            $("input[name=upToken]").val(result.content);
            return true;
        },
        error: function (request) {
            alert("Connection error");
        }
    })
}

// 点击验证码
function sendCode2() {
    var phone = $("#upPhone").val();
    var phoneReg = /(^1\d{10}$)|(^[0-9]\d{7}$)/;
    if (phone == '') {
        layer.msg('请填写手机号！', {icon: 5, time: 1000, shift: 6})
        return false;
    }
    if (!phoneReg.test(phone)) {
        layer.msg('手机号码格式错误！', {icon: 5, time: 1000, shift: 6})
        return false;
    }
    checkPhoneHas(phone);
}
function checkPhoneHas(phone){
    $.ajax({
        url:"http://localhost:8080/user/checkHasPhone",
        type:"GET",
        data:{
            'phone':phone
        },
        dataType:"json",
        success:function(result){
            if(result.code != '0001'){
                layer.msg(result.content, {icon: 5, time: 1000,shift : 6})
                return;
            }
            if(result.content == false){
                layer.msg("该手机号还未注册！", {icon: 5, time: 1000,shift : 6})
                return;
            }
            send(phone);
        },
        error: function(request) {
            alert("Connection error");
        }
    })


}
function send(phone){
    $.ajax({
        url:'http://localhost:8080/user/sendCode',
        type:"GET",
        dataType:"json",
        data:{
            "phone":phone
        },
        success:function(result){
            if(result.code!='0001'){
                layer.msg(result.content, {icon: 5, time: 1000,shift : 6})
                return;
            }
            layer.msg("发送验证码成功！", {icon: 6, time: 1000})
        },
        error: function(request) {
            alert("Connection error");
        }
    })

}

function upPassword(phone,password,token){
    $.ajax({
        url:'http://localhost:8080/user/pwd',
        type:"PUT",
        dataType:"json",
        data:{
            "phone":phone,
            "password":password,
            "token":token
        },
        success:function(result){
            if(result.code!='0001'){
                layer.msg(result.content, {icon: 5, time: 1000,shift : 6})
                return;
            }
            $('#upPwdModal').modal('hide')
            $("#upPwdModal #upPhone").val(phone);
            layer.msg("修改密码成功！", {icon: 6, time: 1000})
            $("#upPwd").val("");
            $("#upYzm").val("");
            $("input[name=upToken]").val("");

            $("#spanPhone").text("手机号: "+phone)
        },
        error: function(request) {
            alert("Connection error");
        }
    })

}

// --------修改手机号-----
function openPhoneModal() {
    $('#upPhoneModal').modal('show')
}

function upPhoneBtn(){
    var phone = $("#newPhone").val();
    var phoneReg = /(^1\d{10}$)|(^[0-9]\d{7}$)/;
    if(phone == ''){
        layer.msg("请填写手机号！", {icon: 5, time: 1000,shift : 6})
        return false;
    }
    if(!phoneReg.test(phone)) {
        layer.msg("手机号码格式错误！", {icon: 5, time: 1000,shift : 6})
        return false;
    }
    var code = $("#newYzm").val();
    if(code == ''){
        layer.msg("请填写验证码！", {icon: 5, time: 1000,shift : 6})
        return false;
    }
    if(code.length != 6){
        layer.msg("验证码错误！", {icon: 5, time: 1000,shift : 6})
        return false;
    }
    var token = $("input[name=phoneToken]").val();
    var isTrueCode = true
    if(token == ''){
        checkCode3(phone,code);
    }
    token = $("input[name=phoneToken]").val();
    if(token == ''){
        return false;
    }
    upPhone(phone,token);
}

function upPhone(phone,token){
    $.ajax({
        url: "http://localhost:8080/user/phone",
        type: "PUT",
        data: {
            "phone": phone,
            "token": token
        },
        dataType: "json",
        async: false,
        success: function (result) {
            if(result.code!='0001'){
                layer.msg(result.content, {icon: 5, time: 1000,shift : 6})
                return;
            }
            $('#upPhoneModal').modal('hide')
            layer.msg("修改手机号成功！", {icon: 6, time: 1000})
            $("input[name=phoneToken]").val("");
            $("#spanPhone").text("手机号: "+phone)
            $("#upPhone").val(phone)
        },
        error: function (request) {
            alert("Connection error");
        }
    })
}
// 点击验证码
function sendCode3() {
    var phone = $("#newPhone").val();
    var phoneReg = /(^1\d{10}$)|(^[0-9]\d{7}$)/;
    if (phone == '') {
        layer.msg('请填写手机号！', {icon: 5, time: 1000, shift: 6})
        return false;
    }
    if (!phoneReg.test(phone)) {
        layer.msg('手机号码格式错误！', {icon: 5, time: 1000, shift: 6})
        return false;
    }
    checkPhoneUnique(phone);
}
function checkCode3(phone, code) {
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
            $("input[name=phoneToken]").val(result.content);
            return true;
        },
        error: function (request) {
            alert("Connection error");
        }
    })
}

function checkPhoneUnique(phone){
    $.ajax({
        url:"http://localhost:8080/user/checkPhoneUnique",
        type:"GET",
        data:{
            'phone':phone
        },
        dataType:"json",
        success:function(result){
            if(result.code != '0001'){
                layer.msg(result.content, {icon: 5, time: 1000,shift : 6})
                return;
            }
            if(result.content == false){
                layer.msg("手机号码已经被占用！", {icon: 5, time: 1000,shift : 6})
                return;
            }
            send(phone);
        },
        error: function(request) {
            alert("Connection error");
        }
    })


}