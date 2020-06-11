$(function () {
    layui.use('layer',function(){

    })
})
// layui.use('layer',function(){
//
// })


function sortmanage(page,size){
    $.ajax({
        url: "http://localhost:8080/sort/user",
        type: "GET",
        dataType: "json",
        async: false,
        data:{
          "page":page,
          "size":size
        },
        success: function (result) {
            console.log(result)
            var code = result.code;
            if(code != '0001'){
                layer.msg(result.content, {icon: 5, time: 1000,shift : 6})
                return;
            }
            var result = result.content;
            var content = result.content;
            var html = '<div class="list-group" id="sort-show">';
            for(var i = 0;i<content.length;i++){
                html += '        <div class="list-group-item">\n' +
                    '            <h3 class="list-group-item-heading">'+content[i].sortName+'</h3>\n' +
                    '            <div>\n' +
                    '                <a href="javascript:void(0)" sid="'+content[i].id+'" class="sort-edit"><span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>重命名</a>\n' +
                    '                <a href="javascript:void(0)" sid="'+content[i].id+'" class="sort-delete"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span>删除</a>\n' +
                    '            </div>\n' +
                    '        </div>';
            }
            html += '</div>';
            $("#result").empty();
            $("#result").append(html);

            //绑定删除事件
            $("#sort-show .sort-delete").on("click",function(){
                var sid = $(this).attr("sid");
                layer.confirm('删除分类将会导致该分类下文章归为默认分类，是否继续删除？', {
                    btn: ['确定','取消'] //按钮
                }, function(){
                    layer.closeAll('dialog');
                    sortDelete(sid);
                });
            })
            //绑定重命名事件
            $("#sort-show .sort-edit").on("click",function(){
                var sid = $(this).attr("sid");
                var sortname = $(this).parent().prev().text();
                $('#sortModalEdit').attr("sid", sid);
                $("#sortModal input").val(sortname);
                $('#sortModal').modal();
            })

            //分页
            paging(result);
            bindSortPage();
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
function bindSortPage(){
    $("#paging li").on("click",function(){
        var disabled = $(this).hasClass("disabled");
        var active = $(this).hasClass("active");
        if(disabled || active){
            return;
        }
        var pid = $(this).attr("pid");
        sortmanage(pid,10);
    })
}


//模态框确认修改按钮
function updateSort(){
    var sortId = $("#sortModalEdit").attr("sid");
    var sortName = $("#sortModal input").val();
    sortEdit(sortId,sortName);
}


function sortDelete(sortId){
    $.ajax({
        url: "http://localhost:8080/sort/delete/"+sortId,
        type: "DELETE",
        dataType: "json",
        async: false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if(code != '0001'){
                layer.msg(result.content, {icon: 5, time: 1000,shift : 6})
                return;
            }
            layer.msg("删除成功！", {icon: 6, time: 2000})
            page = 0;
            size = 10;
            sortmanage(page,size);
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

function sortEdit(sortId,sortName){
    $.ajax({
        url: "http://localhost:8080/sort/update",
        type: "PUT",
        dataType: "json",
        async: false,
        data:{
          "sortId":sortId,
          "sortName":sortName
        },
        success: function (result) {
            console.log(result)
            var code = result.code;
            if(code != '0001'){
                layer.msg(result.content, {icon: 5, time: 1000,shift : 6})
                return;
            }
            layer.msg("修改成功！", {icon: 6, time: 2000})
            $("#sort-show .sort-edit[sid="+sortId+"]").parent().prev().text(sortName);
            $("#sortModal").modal("hide")
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

