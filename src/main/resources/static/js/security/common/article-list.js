var saveType;

$(function () {

})
layui.use('layer',function(){
    getArticleListByType(saveType,0,10);

    $("#article-type li").click(function(){
        var type = $(this).attr("articleType");
        saveType = type;
        getArticleListByType(type,0,10);
    })

    $(".deleteArticle").on("click",function(){
        var id = $(this).parents(".article-option").attr("article-id");
        deleteArticle(id)
    })



})
function getArticleListByType(type,page,size){
    $.ajax({
        url: "http://localhost:8080/article/user",
        type: "GET",
        dataType: "json",
        data:{
            "type":type,
            "page":page,
            "size":size
        },
        async: false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if(code != '0001'){
                layer.msg(result.content, {icon: 5, time: 1000,shift : 6})
                return;
            }
            var result = result.content;
            var content = result.content;

            var html = '<div class="list-group" id="article-show">';
            for(var i = 0 ;i< content.length;i++){
                html +='        <div class="list-group-item">\n' +
                    '            <h3 class="list-group-item-heading"><a style="text-decoration:none" href="/details/'+content[i].id+"/"+saveUser.id+'" target="_blank">'+content[i].title+'</a></h3>\n' +
                    '            <div class="article-list">\n' +
                    '                <div class="article-attribute">\n' +
                    '                    <span style="">'+content[i].createtime+'</span>\n' +
                    '                    <span class="glyphicon glyphicon-thumbs-up" aria-hidden="true"></span>' +content[i].goodTimes+
                    '                    <span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span>' +content[i].viewTimes+
                    '                    <span class="glyphicon glyphicon-comment" aria-hidden="true"></span>' +content[i].commentTimes+
                    '                </div>\n' +
                    '                    <div class="article-option" article-id="'+content[i].id+'">\n' +
                    '                    <div>';
                if(content[i].type == 1){
                    html += '<select class="form-control typeChange">' +
                        '<option value="0"><span class="glyphicon glyphicon-eye-open" aria-hidden="true" ></span>公开</option>' +
                        '<option value="1" selected><span class="glyphicon glyphicon-eye-close" aria-hidden="true"></span>隐藏</option>' +
                        '</select>';
                }else{
                    html += '<select class="form-control typeChange">' +
                        '<option  value="0" selected><span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span>公开</option>' +
                        '<option  value="1"><span class="glyphicon glyphicon-eye-close" aria-hidden="true"></span>隐藏</option>' +
                        '</select>';
                }
                if(content[i].overhead == 1){
                   html += '</div><div>' +
                       '<a href="javascript:void(0)" class="overheadChange" overhead="1"><span class="glyphicon glyphicon-star" aria-hidden="true"></span>顶置</a>';
                }else{
                    html += '</div><div>' +
                        '<a href="javascript:void(0)" class="overheadChange" overhead="0"><span class="glyphicon glyphicon-star-empty" aria-hidden="true"></span>顶置</a>';
                }
                html +='                        <a href="/edit/'+content[i].id+'" target="_blank"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span>编辑</a>\n' +
                    '                        <a href="javascritp:void(0)" class="deleteArticle"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span>删除</a>\n' +
                    '                    </div>\n' +
                    '                </div>'+
                    '            </div>\n' +
                    '        </div>';
            }
            html += '</div>';
            $("#result").empty();
            $("#result").append(html);
            $(".typeChange").on("change",function(){
                var type = $(this).val();
                var id = $(this).parents(".article-option").attr("article-id");
                changeType(id,type)
            })

            $(".overheadChange").on("click",function(){
                var overheadValue = $(this).attr("overhead")
                overheadValue = overheadValue == 1?0:1;
                var id = $(this).parents(".article-option").attr("article-id");
                overhead(id,overheadValue)
            })
            //分页
            paging(result);
            bindArticleListByTypePage();
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

function bindArticleListByTypePage(){
    $("#paging li").on("click",function(){
        var disabled = $(this).hasClass("disabled");
        var active = $(this).hasClass("active");
        if(disabled || active){
            return;
        }
        var pid = $(this).attr("pid");
        getArticleListByType(saveType, pid,10);
    })
}

function deleteArticle(id){
    $.ajax({
        url: "http://localhost:8080/article/delete/"+id,
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
            layer.msg("删除成功！", {icon: 6, time: 1000})
            getArticleListByType(saveType,0,10);
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

function overhead(articleId,overhead){
    $.ajax({
        url: "http://localhost:8080/article/changeOverhead",
        type: "PUT",
        dataType: "json",
        data:{
          "articleId":articleId,
          "overhead":overhead
        },
        async: false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if(code != '0001'){
                layer.msg(result.content, {icon: 5, time: 1000,shift : 6})
                return;
            }
            layer.msg("操作成功！", {icon: 6, time: 1000})
            getArticleListByType(saveType,0,10);
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

function changeType(articleId,type){
    $.ajax({
        url: "http://localhost:8080/article/changeType",
        type: "PUT",
        dataType: "json",
        data:{
            "articleId":articleId,
            "type":type
        },
        async: false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if(code != '0001'){
                layer.msg(result.content, {icon: 5, time: 1000,shift : 6})
                return;
            }
            layer.msg("操作成功！", {icon: 6, time: 1000})
            getArticleListByType(saveType,0,10);
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