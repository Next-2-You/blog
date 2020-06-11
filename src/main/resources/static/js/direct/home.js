var saveUserId = null;
$(function () {

})
layui.use('layer',function(){
    var href = window.location.href.split("/");
    saveUserId = href[href.length-1];
    getUserArticles(saveUserId,0,10);
})
function getUserArticles(id,page,size){
    $.ajax({
        url: "http://localhost:8080/article/user/"+id,
        type: "GET",
        dataType: "json",
        async: false,
        data:{
          'page':page,
          'size':size
        },
        success: function (result) {
            console.log(result)
            var code = result.code;
            if(code != '0001'){
                layer.msg(result.msg, {icon: 5, time: 1000,shift : 6})
                return;
            }

            var content = result.content.content;
            var html = '';
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
            $("#article-list .list-group").empty()
            $("#article-list .list-group").append(html)

            paging(result.content);
            bindHomeArticlePage();
        },
        error: function (request) {
            alert("Connection error");
        }
    })

}
function bindHomeArticlePage(){
    $("#paging li").on("click",function(){
        var disabled = $(this).hasClass("disabled");
        var active = $(this).hasClass("active");
        if(disabled || active){
            return;
        }
        var pid = $(this).attr("pid");
        getUserArticles(saveUserId,pid,10);
    })
}