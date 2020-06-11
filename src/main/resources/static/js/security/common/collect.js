$(function () {

})
layui.use('layer',function(){




})

function getCollection(page,size){
    $.ajax({
        url: "http://localhost:8080/collection",
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
            if (code != '0001') {
                layer.msg(result.msg, {icon: 5, time: 1000, shift: 6})
                return;
            }
            var content = result.content.content;
            var html = '<div class="list-group" id="collection-show">';
            for(var i=0;i<content.length;i++){
                html += '<div class="list-group-item">\n' +
                    '                            <h4 class="list-group-item-heading"><a href="/details/'+content[i].articleId+"/"+content[i].authorId+'" target="_blank">'+content[i].title+'</a></h4>\n' +
                    '                            <span>'+content[i].createtime+'</span>\n' +
                    '                            <a href="javascript:void(0)" cid="'+content[i].id+'" class="cancel-collect"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span>取消收藏</a>\n' +
                    '                        </div>';
            }
            html += '</div>';
            $("#result").empty();
            $("#result").append(html);

            $(".cancel-collect").on("click",function(){
                var cid = $(this).attr("cid");
                cancelCollection(cid);
            })

            //分页
            paging(result.content);
            bindCollectionPage();
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
function bindCollectionPage(){
    $("#paging li").on("click",function(){
        var disabled = $(this).hasClass("disabled");
        var active = $(this).hasClass("active");
        if(disabled || active){
            return;
        }
        var pid = $(this).attr("pid");
        getCollection(pid,10)
    })
}

function cancelCollection(cid){
    $.ajax({
        url: "http://localhost:8080/collection/"+cid,
        type: "DELETE",
        dataType: "json",
        async: false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if (code != '0001') {
                layer.msg(result.msg, {icon: 5, time: 1000, shift: 6})
                return;
            }
            layer.msg("删除成功！", {icon: 6, time: 2000})
            getCollection(0,10);
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