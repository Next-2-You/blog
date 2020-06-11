var headBaseUrl = "http://192.168.184.120:8888/";
$(function () {


})
function uploadFile(file){
    $.ajax({
        url: "http://localhost:8080/user/uploadImg",
        type: "POST",
        dataType: "json",
        data:file,
        processData:false,//默认会转换为可适用于application/x-www-form-urlencoded的数据，设置为false即不处理，直接提交
        contentType:false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if(code != '0001'){
                layer.msg(result.msg, {icon: 5, time: 1000,shift : 6})
                return;
            }

        },
        error: function (request) {
            alert("Connection error");
        }
    })
}


