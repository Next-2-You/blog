var saveSort = null;
$(function () {

})
layui.use('layer',function(){
    var href = window.location.href.split("/");
    getUser(href[href.length-1]);
    getUserNewArticle(href[href.length-1]);
    getBlogId(href[href.length-1]);
})
function getUser(id){
    $.ajax({
        url: "http://localhost:8080/user/users/"+id,
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
            var content = result.content;
            var html = '<img src="'+headBaseUrl+content.headurl+'" alt="'+content.nickname+'" class="img-circle" style="width: 60%">' +
                '                <div class="caption">' +
                '                    <h4 style="margin-bottom: 10px;text-align: center">'+content.nickname+'</h4>' +
                '                    <div class="row">' +
                '                        <div class="col-sm-4 col-md-4" style="text-align: center;"><span class="glyphicon glyphicon-thumbs-up" aria-hidden="true"></span><span style="margin-left: 2px" id="userGoodSum">'+content.goodSum+'</span></div>' +
                '                        <div class="col-sm-4 col-md-4" style="text-align: center;"><span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span><span style="margin-left: 2px" id="userViewSum">'+content.viewSum+'</span></div>' +
                '                        <div class="col-sm-4 col-md-4" style="text-align: center;"><span class="glyphicon glyphicon-comment" aria-hidden="true"></span><span style="margin-left: 2px" id="userCommenSum">'+content.commentSum+'</span></div>' +
                '                    </div>' +
                '                </div>';
            $("#user-info").append(html)
        },
        error: function (request) {
            alert("Connection error");
        }
    })
}
function getUserNewArticle(userId){
    $.ajax({
        url: "http://localhost:8080/article/recent/"+userId,
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
                var html = '<li class="list-group-item"><a href="/details/'+content[i].id+"/"+userId+'">'+content[i].title+'</a></li>';
                $("#article-new").append(html)
            }
        },
        error: function (request) {
            alert("Connection error");
        }
    })
}
function getBlogId(userId){
    $.ajax({
        url: "http://localhost:8080/blog/getByUserId?userId="+userId,
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
            getAricleSort(result.content.id);
        },
        error: function (request) {
            alert("Connection error");
        }
    })
}

function getAricleSort(blogId){
    $.ajax({
        url: "http://localhost:8080/sort/sorts?blogId="+blogId,
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
            var content = result.content;

            for(var i = 0;i<content.length;i++){
                var html = '<li class="list-group-item" sid="'+content[i].id+'"><a href="javascript:void(0)" >'+content[i].sortName+'</a></li>';
                $("#article-sort").append(html)
            }
            $("#article-sort li").on("click",function(){
                page = 0;
                size = 10;
                var id = $(this).attr("sid");
                saveSort = id;
                getSortArticles(id,page,size)
            })
        },
        error: function (request) {
            alert("Connection error");
        }
    })
}

function getSortArticles(id,page,size){
    $.ajax({
        url: "http://localhost:8080/article/sort",
        type: "GET",
        dataType: "json",
        data: {
            "sortId":id,
            "page": page,
            "size": size
        },
        async: false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if(code != '0001'){
                layer.msg(result.msg, {icon: 5, time: 1000,shift : 6})
                return;
            }
            var html = '<div class="list-group">';
            var content = result.content.content;
            for(var i = 0;i<content.length;i++){
                var overhead = content[i].overhead;
                if(overhead){
                    html += '<div class="list-group-item overhead">';
                }else{
                    html += '<div class="list-group-item">';
                }
                html += '<h3 class="list-group-item-heading"><a href="/details/'+content[i].id+'/'+content[i].user.id+'">'+content[i].title+'</a></h3>' +
                    '                        <p class="list-group-item-text">' +content[i].content+'</p>' +
                    '                        <div class="row">' +
                    '                            <div class="col-sm-8 col-md-9">' +
                    '                                <span>'+content[i].createtime+'</span>' +
                    '                            </div>' +
                    '                            <div class="col-sm-8 col-md-3">' +
                    '                                <span class="glyphicon glyphicon-thumbs-up" aria-hidden="true"></span>' +content[i].goodTimes+
                    '                                <span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span>' +content[i].viewTimes+
                    '                                <span class="glyphicon glyphicon-comment" aria-hidden="true"></span>' +content[i].commentTimes+
                    '                            </div>\n' +
                    '                        </div>\n' +
                    '                    </div>';


            }
            html +='</div><div id="paging"></div>';
            $("#article-list").empty();
            $("#article-list").append(html)

            //分页
            paging(result.content);
            bindSortArticleListPage();
        },
        error: function (request) {
            alert("Connection error");
        }
    })

}

function bindSortArticleListPage(){
    $("#paging li").on("click",function(){
        var disabled = $(this).hasClass("disabled");
        var active = $(this).hasClass("active");
        if(disabled || active){
            return;
        }
        var pid = $(this).attr("pid");
        getSortArticles(saveSort, pid,10);
    })
}