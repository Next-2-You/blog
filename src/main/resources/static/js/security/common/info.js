var uploadImgFlag = false;

$(function () {

    //每次显示模态框之前要先把所有内容清理一遍
    $("#addTeachingMessageModel").on("show.bs.modal", function () {

        $("#teacherImage").attr("src", $("#show-headurl").attr("src"));
        $("#personnelNameModal").text($("#show-account").text())
        $("#inputBriefIntr").text($("#show-briefIntr").text())
        $("#personnelNumber").val($("#show-nickname").text())
        $("#inputBlogName").val($("#show-blogName").text())
        $("#inputDescription").text($("#show-description").text())

        $(this).removeData("bs.modal");
    });

    //    图片裁剪插件初始化
    var options =
        {
            thumbBox: '.thumbBox',
            spinner: '.spinner',
            imgSrc: ''
        }
    var cropper = $('.imageBox').cropbox(options);
    $('#file').on('change', function(){
        // $("#addTeachingMessageModel").modal("hide")
        $("#addTeachingMessageModel").css("opacity", 0);
        $("#cutImage").modal("show");
        var reader = new FileReader();
        reader.onload = function(e) {
            options.imgSrc = e.target.result;
            cropper = $('.imageBox').cropbox(options);
            $("#teacherImage").removeClass("hidden");
            // $("#teacherPicName").val(e.target.result)
            $(".uploadPhotos").removeClass("hiddenWarning");
            $(".uploadPhotos").addClass("hiddenWarning");
        }
        reader.readAsDataURL(this.files[0]);
        // this.files = [];
        $("#file").val("");
    })

    $('#btnCrop').on('click', function(){
        var img = cropper.getDataURL();
        // $('.cropped').append('<img src="'+img+'">');
        $("#teacherImage").removeClass("hidden");
        $("#teacherImage").attr('src',img);
        $("#teacherImage").css('width',"165px");
        $("#teacherImage").css('height',"165px");
        // $("#addTeachingMessageModel").modal("show")
        $("#cutImage").modal("hide");
        $("#addTeachingMessageModel").css("opacity", 1);
        uploadImgFlag = true;
    })



//    自己写
    getUserDetail()




})


//点击 上传图片按钮，会跳出一个选择照片的框
function uploadPhotos() {
    $("#file").click();
}
// 裁剪图片的那个框选择取消按钮
function backtoChose() {
    $("#addTeachingMessageModel").css("opacity", 1);
    $("#cutImage").modal("hide")
}
function hideImg() {
    $("#teacherImage").addClass('hidden');
}

function update() {
    $("#addTeachingMessageModel .modal-header .modal-title").text('修改内容');
    //显示模态框
    $("#addTeachingMessageModel").modal("show");
    return false;
}
layui.use('layer',function(){


})

//    自己写
function getUserDetail(){
    $.ajax({
        url: "http://localhost:8080/user/getUserDetail",
        type: "GET",
        dataType: "json",
        async: false,
        success: function (result) {
            console.log(result)
            var user = result.content;
            if(user != null){
                var html = '    <div class="row">\n' +
                    '        <div class="col-sm-12 col-md-4">\n' +
                    '            <div style="width: 200px;height: 200px;margin: 0 auto;">\n' +
                    '                <img src="'+headBaseUrl+user.headurl+'" alt="头像" id="show-headurl"' +
                    '                     style="width:auto;height:auto;max-height: 200px;max-width: 200px;">\n' +
                    '            </div>\n' +
                    '        </div>\n' +
                    '        <div class="col-sm-12 col-md-8">\n' +
                    '            <form class="form-horizontal">\n' +
                    '                <div class="form-group">\n' +
                    '                    <label class="col-sm-2 control-label">账号</label>\n' +
                    '                    <div class="col-sm-10">\n' +
                    '                        <p class="form-control-static" id="show-account">'+user.account+'</p>\n' +
                    '                    </div>\n' +
                    '                </div>\n' +
                    '                <div class="form-group">\n' +
                    '                    <label class="col-sm-2 control-label">手机号</label>\n' +
                    '                    <div class="col-sm-10">\n' +
                    '                        <p class="form-control-static" id="show-phone">'+user.phone+'</p>\n' +
                    '                    </div>\n' +
                    '                </div>\n' +
                    '                <div class="form-group">\n' +
                    '                    <label class="col-sm-2 control-label">昵称</label>\n' +
                    '                    <div class="col-sm-10">\n' +
                    '                        <p class="form-control-static" id="show-nickname">'+user.nickname+'</p>\n' +
                    '                    </div>\n' +
                    '                </div>\n' +
                    '                <div class="form-group">\n' +
                    '                    <label class="col-sm-2 control-label">个性签名</label>\n' +
                    '                    <div class="col-sm-10">\n' +
                    '                        <p class="form-control-static" id="show-briefIntr">'+user.briefIntr+'</p>\n' +
                    '                    </div>\n' +
                    '                </div>\n' +
                    '                <div class="form-group">\n' +
                    '                    <label class="col-sm-2 control-label">博客名称</label>\n' +
                    '                    <div class="col-sm-10">\n' +
                    '                        <p class="form-control-static" id="show-blogName">'+user.blogName+'</p>\n' +
                    '                    </div>\n' +
                    '                </div>\n' +
                    '                <div class="form-group">\n' +
                    '                    <label class="col-sm-2 control-label">博客简介</label>\n' +
                    '                    <div class="col-sm-10">\n' +
                    '                        <p class="form-control-static" id="show-description">'+user.description+'</p>\n' +
                    '                    </div>\n' +
                    '                </div>\n' +
                    '                <div class="form-group">\n' +
                    '                    <label class="col-sm-2 control-label"></label>\n' +
                    '                    <div class="col-sm-10">\n' +
                    '                        <p class="btn btn-primary btn-lg" data-toggle="modal" onclick="update()">\n' +
                    '                            编辑</p>\n' +
                    '                    </div>\n' +
                    '                </div>\n' +
                    '            </form>\n' +
                    '        </div>\n' +
                    '    </div>';
                $("#result").empty();
                $("#result").append(html);
            }
            $("#paging").empty()

            // $("#show-account").text(user.account);
            // $("#show-phone").text(user.phone)
            // $("#show-nickname").text(user.nickname)
            // $("#show-briefIntr").text(user.briefIntr)
            // $("#show-headurl").attr("src",headBaseUrl+user.headurl)
            // $("#show-blogName").text(user.blogName)
            // $("#show-description").text(user.description)


            //暂时
            $("#personnelNameModal").text(user.account)
            $("#inputBriefIntr").text(user.briefIntr)
            $("#personnelNumber").val(user.nickname)
            $("#inputBlogName").val(user.blogName)
            $("#inputDescription").text(user.description)
            $("#teacherImage").removeClass("hidden");
            $("#teacherImage").attr('src',headBaseUrl+user.headurl);
            $("#teacherImage").css('width',"165px");
            $("#teacherImage").css('height',"165px");

            $("#loginInfo img").attr("src",headBaseUrl+user.headurl)

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
function updateUserInfo(){
    var data = {};

    var img = $("#teacherImage").attr("src").split(";base64,");
    var imgBase64 = img[1];
    var imgType = img[0].split("/")[1];
    var nickname = $("#personnelNumber").val();
    var briefIntr = $("#inputBriefIntr").val();
    var blogName = $("#inputBlogName").val();
    var description = $("#inputDescription").val();

    if(nickname == ""){
        layer.msg("昵称不能为空！", {icon: 5, time: 1000,shift : 6})
        return;
    }
    if(blogName == ""){
        layer.msg("博客名称不能为空！", {icon: 5, time: 1000,shift : 6})
        return;
    }


    data.imgBase64 = imgBase64;
    data.imgType = imgType;
    data.nickname = nickname;
    data.briefIntr = briefIntr;
    data.blogName = blogName;
    data.description = description;

    $.ajax({
        url: "http://localhost:8080/user/update",
        type: "PUT",
        dataType: "json",
        data:data,
        async: false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if(code != '0001'){
                layer.msg(result.content, {icon: 5, time: 1000,shift : 6})
                return;
            }
            layer.msg("更新成功", {icon: 6, time: 2000})
            //隐藏不了
            // $('#addTeachingMessageModel').modal('hide');
            $("#modalClose").click()

            getUserDetail()

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



// function uploadImg(){
//     var img = new FormData($("#form_add")[0]);
//     uploadFile(img);
// }

function getHeadUrl(){
    $.ajax({
        url: "http://localhost:8080/user/loginInfo",
        type: "GET",
        dataType: "json",
        async: false,
        success: function (result) {
            console.log(result)
            var user = result.content;
            if(user != null){
                $("#loginInfo img").attr("src",headBaseUrl+user.headurl)
                $("#show-headurl").attr("src",headBaseUrl+user.headurl)
                $("#teacherImage").attr("src",headBaseUrl+user.headurl)
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


