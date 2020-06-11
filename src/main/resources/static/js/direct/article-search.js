var sortsave = null;
var timelimitsave = null;
$(function () {

})
layui.use('layer',function(){
    var title = localStorage.getItem("title");
    localStorage.removeItem("title");
    if(title == ""){
        title = $("#search").val()
    }else{
        $("#search").val(title)
    }

    searchArticle(title,null,null,null,null)
    $("#sort-info").children(":not(:last)").click(function(){
        var sort = $(this).attr("sort")
        sortsave = sort;
        var title = $("#search").val()
        searchArticle(title,timelimitsave,sort,null,null)
        $(this).siblings().css({"background-color":"white"})
        $(this).css({"background-color":"#eee"})

    })

    $("#time-limit li").click(function(){
        var limitTimeType = $(this).attr("limitTimeType");
        timelimitsave = limitTimeType;
        var title = $("#search").val()
        searchArticle(title,limitTimeType,sortsave,null,null)
        $("#show-limit").text($(this).children("a").text());
    })

})
function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        if (pair[0] == variable) {
            return pair[1];
        }
    }
    return ("");
}



function searchArticle(title, limitTimeType, sort, page, size) {
    $.ajax({
        url: "http://localhost:8080/article/search?"+sort,
        type: "GET",
        dataType: "json",
        data: {
            "title": title,
            "limitTimeType": limitTimeType,
            "page": page,
            "size": size
        },
        async: false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if (code != '0001') {
                layer.msg(result.msg, {icon: 5, time: 1000,shift : 6})
                return;
            }
            //清空子节点
            $("#search-middle>div").empty()
            var resultContent = result.content.content;
            for (var i = 0; i < resultContent.length; i++) {
                var html = '<div class="list-group-item">\n' +
                    '                    <h3 class="list-group-item-heading"><a href="/details/' + resultContent[i].id + "/" + resultContent[i].user.id + '" target="_blank">' + resultContent[i].title + '</a></h3>\n' +
                    '                    <p class="list-group-item-text">\n' +resultContent[i].content+
                    '                    </p>\n' +
                    '                    <div class="row">\n' +
                    '                        <div class="col-sm-8 col-md-8">\n' +
                    '                            <a href="/home/' + resultContent[i].user.id + '"><img src="' +headBaseUrl+ resultContent[i].user.headurl + '" alt="' + resultContent[i].user.nickname + '" class="img-circle"></a>\n' +
                    '                            <span>' + resultContent[i].user.nickname + '</span>\n' +
                    '                            <span>' + resultContent[i].createtime + '</span>\n' +
                    '                        </div>\n' +
                    '                        <div class="col-sm-8 col-md-4">\n' +
                    '                            <div>\n' +
                    '                                <span class="glyphicon glyphicon-thumbs-up" aria-hidden="true"></span>' + resultContent[i].goodTimes +
                    '                                <span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span>' + resultContent[i].viewTimes +
                    '                                <span class="glyphicon glyphicon-comment" aria-hidden="true"></span>' + resultContent[i].commentTimes +
                    '                            </div>\n' +
                    '                        </div>\n' +
                    '                    </div>\n' +
                    '                </div>';

                $("#search-middle>div").append(html)
            }
            var content = result.content;
            $("#totalCount").text(content.totalElements+" 个结果")
            //分页
            paging(content);
            bindPage()
        },
        error: function (request) {
            alert("Connection error");
        }
    })
}
function bindPage(){

    $("#paging li").on("click",function(){
        var disabled = $(this).hasClass("disabled");
        var active = $(this).hasClass("active");
        if(disabled || active){
            return;
        }
        var title = $("#search").val()
        var pid = $(this).attr("pid");
        searchArticle(title,timelimitsave,sortsave,pid,10)
    })


}
