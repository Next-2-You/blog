var isGood = false;
var isCollection = false;
$(function () {

})
layui.use('layer', function () {
    var href = window.location.href.split("/");
    var aid = href[href.length - 2];
    getArticle(aid);
    getComment(aid, 0,0,4)
    $("#save-articleId").attr("aid", aid);
    getCollectionAndGood(aid);
})

//为回复按钮绑定事件
function bindReply() {
    $(".replyCommnet").on("click", function () {
        console.log("打開模態框")
        var tid = $(this).attr("tid");
        var commentid = $(this).attr("commentid");
        console.log(tid + "," + commentid);
        var name = "";
        if (tid == commentid) {
            name = $(this).parent().prev().find("a span").text();
        } else {
            name = $(this).siblings("a").find("span").text();
        }
        console.log("name:" + name)
        $("#myModalLabel").text("回复 " + name);
        $('#replyBtn').attr("tid", tid);
        $("#replyBtn").attr("commentid", commentid);
        $('#myModal').modal();
    })
}


//模态框保存按钮
function modalSaveBtn() {
    var content = $("#modal-textarea").val();
    if (content == null || content.trim() == '') {
        layer.msg("请输入回复内容！", {icon: 5, time: 1000, shift: 6})
    }
    var aid = $("#save-articleId").attr("aid");
    var tid = $('#replyBtn').attr("tid");
    var commentid = $("#replyBtn").attr("commentid");
    writeComment(tid, commentid, content, aid);

}


function getArticle(id) {
    $.ajax({
        url: "http://localhost:8080/article/articles/" + id,
        type: "GET",
        dataType: "json",
        async: false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if (code != '0001') {
                layer.msg(result.msg, {icon: 5, time: 1000, shift: 6})
                return;
            }
            var content = result.content;
            var html = '<h2>' + content.title + '</h2>' +
                '            <span>' + content.createtime + '</span>' +
                '            <span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span>' + content.viewTimes +
                '                <a href="javascript:void(0)"><span class="glyphicon glyphicon-star-empty" aria-hidden="true" id="collectionId"  aid="' + content.id + '" ></span>收藏</a>' +
                '<a href="javascript:void(0)"><span class="glyphicon glyphicon-thumbs-up" aria-hidden="true" id="goodId"  aid="' + content.id + '" ></span>赞</a>';
            var sort = content.sort;
            html += '<div>分类专栏 : ';
            if(sort!=null){
                html += '<a href="javascript:void(0)" onclick="getSortArticles(' + sort.id + ',0,10)">' + sort.sortName + '</a></div>';
            }else{
                html += '无</div>';
            }
            html += '<div>标签 : ';
            var tagList = content.tagList;
            if(tagList.length>0){
                for(var i=0;i<tagList.length;i++){
                    html += '<span class="label label-info" style="margin: 0 2px;font-size: 13px;">'+tagList[i].tagName+'</span>';
                }
                html += '</div>';
            }else{
                    html += '无</div>';
            }

            $("#article-info").append(html);

            $("#article-content>textarea").text(content.content);
            //markdown语言
            var testEditor = editormd.markdownToHTML("article-content", {//注意：这里是上面DIV的id
                htmlDecode: "style,script,iframe",
                emoji: true,
                taskList: true,
                tocm: true,
                tex: true, // 默认不解析
                flowChart: true, // 默认不解析
                sequenceDiagram: true, // 默认不解析
                codeFold: true
            });

            //为点赞和收藏绑定点击事件
            $("#goodId").on("click",function(){
                var aid = $(this).attr("aid");
                if(isGood){
                    deleteGood(aid);
                    return;
                }
                addGood(aid)
            })
            $("#collectionId").on("click",function(){
                var aid = $(this).attr("aid");
                if(isCollection){
                    deleteCollection(aid);
                    return;
                }
                addCollection(aid)
            })
        },
        error: function (request) {
            alert("Connection error");
        }
    })
}

// 一级评论 数据库tid=0，回复一级评论tid=一级评论id
function getComment(articleId, tid,page,size) {
    $.ajax({
        url: "http://localhost:8080/comment/comments",
        type: "GET",
        dataType: "json",
        data: {
            'articleId': articleId,
            'tid': tid,
            'page':page,
            'size':size
        },
        async: false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if (code != '0001') {
                layer.msg(result.msg, {icon: 5, time: 1000, shift: 6})
                return;
            }
            $("#comment-content").empty();
            var resultContent = result.content;
            var content = resultContent.content;
            var html = '';
            for (var i = 0; i < content.length; i++) {
                html += '<div class="row">' +
                    '                            <div class="col-xs-1 col-sm-1 col-md-1">' +
                    '                                <a target="_blank" href="/home/' + content[i].user.id + '">' +
                    '                                    <img src="' + headBaseUrl + content[i].user.headurl + '"alt="' + content[i].user.nickname + '" class="img-circle"/>' +
                    '                                </a>\n' +
                    '                            </div>\n' +
                    '                            <div class="col-xs-11 col-sm-11 col-md-11">\n' +
                    '                                <div class="source">\n' +
                    '                                    <div>\n' +
                    '                                        <div>\n' +
                    '                                            <a target="_blank" href="/home/' + content[i].user.id + '">\n' +
                    '                                                <span class="name ">' + content[i].user.nickname + '</span>\n' +
                    '                                            </a>\n' +
                    '                                            <span class="date" title="' + content[i].createtime + '">' + content[i].createtime + '</span>\n' +
                    '                                        </div>\n' +
                    '                                        <div>\n' +
                    //注意这里tid=id
                    '                                            <a href="javascript:void(0)" class="replyCommnet" tid="' + content[i].id + '" commentid="' + content[i].id + '">回复</a>\n';
                if (content[i].replyCount > 0) {
                    html += '<a href="javascript:void(0);" aId="' + articleId + '" tid="' + content[i].id + '" class="showReply">查看回复(' + content[i].replyCount + ')</a>';
                }
                html += '</div>\n' +
                    '                                    </div>\n' +
                    '                                    <div>\n' +
                    '                                        <span class="new-comment">' + content[i].reply + '</span>\n' +
                    '                                    </div>\n' +
                    '                                </div>\n' +
                    '                            </div>\n' +
                    '                        </div>\n' +
                    '                        <hr>'

            }
            $("#comment-content").append(html);
            //分页
            paging(resultContent);
            bindCommentPage();
            bindReply();
            //查看回复
            $(".showReply").on("click", function () {
                var aid = $(this).attr("aid");
                var tid = $(this).attr("tid");
                showReplay(aid, tid, this);
            })
        },
        error: function (request) {
            alert("Connection error");
        }
    })
}

//查看回复 tid = 一级评论的id
function showReplay(articleId, tid, point) {
    $.ajax({
        url: "http://localhost:8080/comment/replys",
        type: "GET",
        dataType: "json",
        data: {
            'articleId': articleId,
            'tid': tid
        },
        async: false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if (code != '0001') {
                layer.msg(result.msg, {icon: 5, time: 1000, shift: 6})
                return;
            }
            var resultContent = result.content;
            var html = '<div class="reply">';
            for (var i = 0; i < resultContent.length; i++) {
                html += '<div class="row">' +
                    '       <div class="col-xs-1 col-sm-1 col-md-1">' +
                    '           <a href="/home/' + resultContent[i].user.id + '">' +
                    '               <img src="' + headBaseUrl + resultContent[i].user.headurl + '"alt="' + resultContent[i].user.nickname + '" class="img-circle" />' +
                    '           </a>' +
                    '       </div>' +
                    '       <div class="col-xs-11 col-sm-11 col-md-11">' +
                    '           <div>' +
                    '               <a target="_blank" href="/home/' + resultContent[i].user.id + '">' +
                    '                   <span class="name ">' + resultContent[i].user.nickname + '</span>' +
                    '               </a> 回复 ' + resultContent[i].nickname +
                    '               <span class="date" title="' + resultContent[i].createtime + '" >' + resultContent[i].createtime + '</span>' +
                    '               <a href="javascript:void(0)" class="replyCommnet" tid="' + resultContent[i].tid + '" commentid="' + resultContent[i].id + '">回复</a>' +
                    '           </div>' +
                    '       <div>' +
                    '       <span>' + resultContent[i].reply + '</span>' +
                    '       </div>' +
                    '</div></div>';
            }
            html += '</div>';
            $(point).parents(".source").after(html);
            $(point).remove();
            bindReply();
        },
        error: function (request) {
            alert("Connection error");
        }
    })


}


//发表评论
function publishComment() {
    var content = $("#reply-content>textarea").val();
    var aid = $("#save-articleId").attr("aid");
    if (content == null || content.trim() == '' || aid == null) {
        return;
    }
    writeComment(0, 0, content, aid);
}


function writeComment(tid, cid, reply, articleId) {
    $.ajax({
        url: "http://localhost:8080/comment/reply",
        type: "POST",
        dataType: "json",
        data: {
            'article.id': articleId,
            'tid': tid,
            'cid': cid,
            'reply': reply
        },
        async: false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if (code != '0001') {
                layer.msg(result.msg, {icon: 5, time: 1000, shift: 6})
                return;
            }
            if (tid == 0) {
                $("#reply-content>textarea").val("");
                layer.msg("评论成功！", {icon: 6, time: 2000})
                var aid = $("#save-articleId").attr("aid");
                getComment(aid, 0,0,4);
            } else {
                $("#myModal").modal("hide")
                layer.msg("回复成功！", {icon: 6, time: 2000})
                var addJD = $(".replyCommnet[commentid=" + tid + "]" + "[tid=" + tid + "]");
                if (addJD.next().length > 0) {
                    $(".showReply[aid=" + articleId + "]" + "[tid=" + tid + "]").click();
                } else {
                    var html = '<a href="javascript:void(0);" aid="' + articleId + '" tid="' + tid + '" class="showReply"></a>';
                    var jd = $(html);
                    addJD.append(jd);
                    addJD.parents(".source").next().remove();
                    showReplay(articleId, tid, jd);
                }
            }
            $("#myModal textarea").val("");
            var commenSum = $("#userCommenSum").text();
            $("#userCommenSum").text(parseInt(commenSum)+1)
        },
        error: function (request) {
            var code = request.responseJSON.code;
            if (code == "0004") {
                layer.confirm('您还未登陆，现在去登陆？', {
                    btn: ['确定', '取消'] //按钮
                }, function () {
                    layer.closeAll('dialog');
                    window.location.href = "/logoreg";
                });
                return;
            }
            layer.msg(request.responseJSON.msg, {icon: 5, time: 1000, shift: 6})
        }
    })

}

//查看是否有收藏和点赞
function getCollectionAndGood(articleId) {
    $.ajax({
        url: "http://localhost:8080/user/articleInfo/" + articleId,
        type: "GET",
        dataType: "json",
        async: false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if (code != '0001') {
                layer.msg(result.msg, {icon: 5, time: 1000, shift: 6})
                return;
            }
            var content = result.content;
            isGood = content.isGood;
            isCollection = content.isCollection;
            if (isGood) {
                $("#goodId").attr("class", "glyphicon glyphicon-star");
            }
            if (isCollection) {
                $("#collectionId").attr("class", "glyphicon glyphicon-heart");
            }


        },
        error: function (request) {

        }
    })


}

//点赞
function addGood(articleId) {
    $.ajax({
        url: "http://localhost:8080/good/add/" + articleId,
        type: "POST",
        dataType: "json",
        async: false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if (code != '0001') {
                layer.msg(result.msg, {icon: 5, time: 1000, shift: 6})
                return;
            }
            $("#goodId").attr("class", "glyphicon glyphicon-star");
            isGood = true;
            var goodSum = $("#userGoodSum").text();
            $("#userGoodSum").text(parseInt(goodSum)+1)
        },
        error: function (request) {
            var code = request.responseJSON.code;
            if (code == "0004") {
                layer.confirm('您还未登陆，现在去登陆？', {
                    btn: ['确定', '取消'] //按钮
                }, function () {
                    layer.closeAll('dialog');
                    window.location.href = "/logoreg";
                });
                return;
            }
            layer.msg(request.responseJSON.msg, {icon: 5, time: 1000, shift: 6})
        }
    })

}

//取消赞
function deleteGood(articleId) {
    $.ajax({
        url: "http://localhost:8080/good/delete/" + articleId,
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
            $("#goodId").attr("class", "glyphicon glyphicon-star-empty");
            isGood = false;
            var goodSum = $("#userGoodSum").text();
            $("#userGoodSum").text(parseInt(goodSum)-1)
        },
        error: function (request) {
            var code = request.responseJSON.code;
            if (code == "0004") {
                layer.confirm('您还未登陆，现在去登陆？', {
                    btn: ['确定', '取消'] //按钮
                }, function () {
                    layer.closeAll('dialog');
                    window.location.href = "/logoreg";
                });
                return;
            }
            layer.msg(request.responseJSON.msg, {icon: 5, time: 1000, shift: 6})
        }
    })

}

//收藏
function addCollection(articleId) {
    $.ajax({
        url: "http://localhost:8080/collection/" + articleId,
        type: "POST",
        dataType: "json",
        async: false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if (code != '0001') {
                layer.msg(result.msg, {icon: 5, time: 1000, shift: 6})
                return;
            }
            $("#collectionId").attr("class", "glyphicon glyphicon-heart");
            isCollection = true;
        },
        error: function (request) {
            var code = request.responseJSON.code;
            if (code == "0004") {
                layer.confirm('您还未登陆，现在去登陆？', {
                    btn: ['确定', '取消'] //按钮
                }, function () {
                    layer.closeAll('dialog');
                    window.location.href = "/logoreg";
                });
                return;
            }
            layer.msg(request.responseJSON.msg, {icon: 5, time: 1000, shift: 6})
        }
    })

}

//取消收藏
function deleteCollection(articleId) {
    $.ajax({
        url: "http://localhost:8080/collection/delete/" + articleId,
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
            $("#collectionId").attr("class", "glyphicon glyphicon-thumbs-up");
            isCollection = false;
        },
        error: function (request) {
            var code = request.responseJSON.code;
            if (code == "0004") {
                layer.confirm('您还未登陆，现在去登陆？', {
                    btn: ['确定', '取消'] //按钮
                }, function () {
                    layer.closeAll('dialog');
                    window.location.href = "/logoreg";
                });
                return;
            }
            layer.msg(request.responseJSON.msg, {icon: 5, time: 1000, shift: 6})
        }
    })

}

function bindCommentPage(){
    $("#paging li").on("click",function(){
        var disabled = $(this).hasClass("disabled");
        var active = $(this).hasClass("active");
        if(disabled || active){
            return;
        }
        var pid = $(this).attr("pid");
        var aid = $("#save-articleId").attr("aid");
        getComment(aid, 0,pid,4);
    })
}