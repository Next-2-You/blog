var saveAid = null;
$(function () {
    var href = window.location.href.split("/");
    var aid = href[href.length-1];
    var number = /^\+?[1-9][0-9]*$/;　　//判断是否为正整数
    var isNumber = number.test(aid);
    //分类下拉框
    showSortList();
    if(isNumber){
        saveAid = aid;
        //检查文章作者
        checkArticleAuthor(aid);
    }else{
        // 初始化标签控件
        $("#tag").tagEditor({
            // initialTags: ['spring','springmvc'],
            maxTags: 5,
            delimiter: ',',
            forceLowercase: false,
            animateDelete: 0,
            placeholder: '请输入标签'
        });
    }
    var editor = editormd("md-editor", {
        width  : "90%",
        height : 720,
        path   : "/editor/lib/",
        theme : "dark",
        // previewTheme : "dark",
        editorTheme : "pastel-on-dark",
        // markdown : "md",
        codeFold : true,
        searchReplace : true,
        saveHTMLToTextarea : true, // 保存 HTML 到 Textarea
        toolbarAutoFixed:true,//工具栏自动固定定位的开启与禁用
        emoji : true,//开启表情
        // watch : false,                // 关闭实时预览
        taskList : true,
        tocm : true,         // Using [TOCM]
        tex : true,                   // 开启科学公式TeX语言支持，默认关闭
        flowChart : true,             // 开启流程图支持，默认关闭
        sequenceDiagram : true,       // 开启时序/序列图支持，默认关闭,
        imageUpload : true,
        imageFormats : ["jpg", "jpeg", "gif", "png", "bmp", "webp"],
        imageUploadURL : "/article/uploadImage",
        onload: function(data){

        }
    });


    //分类新增按钮
    $("#add-new-sort").click(function(){
        var sortName = $("#new-sort").val();
        if(sortName != null && sortName.trim() != ''){
            addNewSort(sortName);
            return;
        }
    })


    //
})
layui.use('layer',function(){
    

})
function getArticle(id){
    $.ajax({
        url: "http://localhost:8080/article/articles/"+id,
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
            //回显标签
            var tagList = content.tagList;
            var tagArray = [];
            if(tagList !=null && tagList.length>0){
                for(var i = 0;i<tagList.length;i++){
                    tagArray.push(tagList[i].tagName);
                }

            }
            // 初始化标签控件
            $("#tag").tagEditor({
                initialTags: tagArray,
                maxTags: 5,
                delimiter: ',',
                forceLowercase: false,
                animateDelete: 0,
                placeholder: '请输入标签'
            });

            //回显标题
            var title = content.title;
            $("#article-title").val(title);
            //回显内容
            $("#content").text(content.content);
            //回显分类
            if(content.sort != null){
                var sortId = content.sort.id;
                $("#sorts option[value="+sortId+"]").attr("selected", true);
            }

            //回显属性
            $("input[type='radio'][name='overheadRadioOptions'][value="+content.overhead+"]").attr('checked','true');
            $("input[type='radio'][name='typeRadioOptions'][value="+content.type+"]").attr('checked','true');
        },
        error: function (request) {
            alert("Connection error");
        }
    })
}
function checkArticleAuthor(articleId) {
    $.ajax({
        url: "http://localhost:8080/article/check/"+articleId,
        type: "GET",
        dataType: "json",
        async: false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if(code != '0001'){
                layer.msg(result.content, {icon: 5, time: 1000,shift : 6})
                setTimeout(function(){window.location.href = 'http://localhost:8080/'},1000)
                return;
            }
            //获取文章
            getArticle(articleId);
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


function checkAticle(){
    //标题
    var title = $("#article-title").val();
    if(title == null ||title.trim() == ''){
        layer.msg("请输入标题！", {icon: 5, time: 1000,shift : 6})
        return;
    }
    //内容
    var content = $('#content').val();
    if(content == null ||content.trim() == ''){
        layer.msg("请输入内容！", {icon: 5, time: 1000,shift : 6})
        return;
    }
    //分类
    var sort = $("#sorts").val();
    //标签
    var tagName = $('#tag').tagEditor('getTags')[0].tags;
    //是否顶置
    var overhead = $("input[type='radio'][name='overheadRadioOptions']:checked").val();
    if(overhead == null ||overhead.trim() == ''){
        layer.msg("请选择是否顶置！", {icon: 5, time: 1000,shift : 6})
        return;
    }
    //是否公开
    var type = $("input[type='radio'][name='typeRadioOptions']:checked").val();
    if(type == null ||type.trim() == ''){
        layer.msg("请选择是否公开！", {icon: 5, time: 1000,shift : 6})
        return;
    }
    console.log("tagName:"+tagName)
    if(saveAid != null){
        updateArticle(saveAid,title,content,sort,tagName,overhead,type);
    }else{
        addArticle(title,content,sort,tagName,overhead,type);
    }
}

function addArticle(title,content,sort,tagName,overhead,type){
    console.log("tagName2:"+tagName)
    $.ajax({
        url: "http://localhost:8080/article/add",
        type: "POST",
        dataType: "json",
        data:{
            "title":title,
            "content":content,
            "tagName":tagName+"",
            "sortId":sort,
            "overhead":overhead,
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
            layer.msg("添加成功！正在跳转到首页。。。", {icon: 6, time: 2000})
            setTimeout(function(){window.location.href = 'http://localhost:8080/'},2000)
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

function addNewSort(sortName){
    $.ajax({
        url: "http://localhost:8080/sort/add",
        type: "POST",
        dataType: "json",
        data:{
          "sortName":sortName
        },
        async: false,
        success: function (result) {
            console.log(result)
            var code = result.code;
            if(code != '0001'){
                layer.msg(result.content, {icon: 5, time: 1000,shift : 6})
                return;
            }
            //清空
            $("#new-sort").val("");
            layer.msg("添加成功！", {icon: 6, time: 1000})
            showSortList();
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


function showSortList(){
    $.ajax({
        url: "http://localhost:8080/sort/choice",
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
            var html = '<option value="-1">其它(默认)</option>';
            for(var i = 0;i<content.length;i++){
                html += '<option value="'+content[i].id+'">'+content[i].sortName+'</option>';
            }
            $("#sorts").empty();
            $("#sorts").append(html);
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


function updateArticle(id,title,content,sort,tagName,overhead,type){
    $.ajax({
        url: "http://localhost:8080/article/update",
        type: "PUT",
        dataType: "json",
        data:{
            "id":id,
            "title":title,
            "content":content,
            "tagName":tagName+"",
            "sortId":sort,
            "overhead":overhead,
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
            layer.msg("更新成功！正在跳转到首页。。。", {icon: 6, time: 2000})
            setTimeout(function(){window.location.href = 'http://localhost:8080/'},2000)
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