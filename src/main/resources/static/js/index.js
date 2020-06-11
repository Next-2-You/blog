var size = 10;
layui.use(['flow','layer'],function(){
    // if(!last){
    //     getLeftData(page,size);
    // }
    getHotArticle();
    getHotUser();
    getHotTag();
    var flow = layui.flow;
    var layer = layui.layer;
    flow.load({
        elem: '#index-left .list-group' //流加载容器
        , scrollElem: '#index-left .list-group' //滚动条所在元素，一般不用填，此处只是演示需要。
        , isAuto:true
        , end : '<p style="text-align:center;color:lightgray;">到底啦！</p>'
        , done: function (page, next) { //执行下一页的回调
            //前端从1开始，后端从0开始
            getLeftData(page-1,size,next);
        }
    });
})

$(function () {



})
// 首页左侧按更新时间排序
function getLeftData(page,size,next){
    $.ajax({
        url: "http://localhost:8080/article/articles",
        type: "GET",
        data: {
            "page": page,
            "size": size
        },
        dataType: "json",
        async: false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if(code !='0001'){
                layer.msg(result.msg, {icon: 5, time: 1000,shift : 6})
                return;
            }
            var content = result.content;
            var resultContent = content.content;
            var html = [];
            for(var i=0;i<resultContent.length;i++){
                var point = '<div class="list-group-item"><h3 class="list-group-item-heading">' +
                    '<a href="/details/'+resultContent[i].id+"/"+resultContent[i].user.id+'">'+resultContent[i].title+'</a></h3><p class="list-group-item-text">'+resultContent[i].content+'</p>' +
                    '<div class="row article-attribute"><div class="col-sm-8 col-md-9"><a href="/home/'+resultContent[i].user.id+'">' +
                    '<img src="'+headBaseUrl+resultContent[i].user.headurl+'" alt="'+resultContent[i].user.nickname+'" class="img-circle"></a>' +
                    '<span style="">'+resultContent[i].user.nickname+'</span><span style="">'+resultContent[i].createtime+'</span></div>' +
                    '<div class="col-sm-8 col-md-3"><span class="glyphicon glyphicon-thumbs-up" aria-hidden="true"></span>'+resultContent[i].goodTimes+
                    '<span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span>'+resultContent[i].viewTimes+
                    '<span class="glyphicon glyphicon-comment" aria-hidden="true"></span>'+resultContent[i].commentTimes+
                    '</div></div></div>';
                html.push(point);
            }
            // $("#index-left .list-group").append(html);
            next(html.join(''), page < content.totalPages);
        },
        error: function (request) {
            alert("Connection error");
        }
    })
}


function getHotArticle(){
    $.ajax({
        url: "http://localhost:8080/article/hot",
        type: "GET",
        dataType: "json",
        async: false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if(code != '0001'){
                layer.msg(result.msg, {icon: 5, time: 1000,shift : 6})
                return;
            }
            var content = result.content.content;
            for(var i = 0;i<content.length;i++){
                var html = '<li class="list-group-item"><a href="/details/'+content[i].id+"/"+content[i].user.id+'">'+content[i].title+'</a></li>';
                $("#article-hot").append(html);
            }
        },
        error: function (request) {
            alert("Connection error");
        }
    })

}

function getHotUser(){
    $.ajax({
        url: "http://localhost:8080/user/hot",
        type: "GET",
        dataType: "json",
        async: false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if(code != '0001'){
                layer.msg(result.msg, {icon: 5, time: 1000,shift : 6})
                return;
            }
            var content = result.content.content;
            for(var i = 0;i<content.length;i++){
                var html = '<li class="list-group-item"><a href="/home/'+content[i].id+'">'+content[i].nickname+'</a></li>';
                $("#user-hot").append(html);
            }
        },
        error: function (request) {
            alert("Connection error");
        }
    })

}
function getHotTag(){
    $.ajax({
        url: "http://localhost:8080/tag/hot",
        type: "GET",
        dataType: "json",
        async: false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if(code != '0001'){
                layer.msg(result.msg, {icon: 5, time: 1000,shift : 6})
                return;
            }
            var content = result.content.content;
            for(var i = 0;i<content.length;i++){
                var html = '<li class="list-group-item"><a tid="'+content[i].id+'" href="javascript:void(0)">'+content[i].tagName+'</a></li>';
                $("#tag-hot").append(html);
            }
        },
        error: function (request) {
            alert("Connection error");
        }
    })

}