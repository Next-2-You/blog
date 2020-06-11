$(function () {


})

function paging(content){
    var first = content.first == true?'disabled':'';
    var last = content.last == true?'disabled':'';
    if(content.totalPages == 0){
        $("#paging").empty()
        return;
    }
    var pageHeader = '<nav aria-label="...">\n' +
        '                <ul class="pagination">\n' +
        '                    <li class="'+first+'" pid="'+(content.pageNo-1)+'">\n' +
        '                      <span>\n' +
        '                        <span aria-hidden="true">&laquo;</span>\n' +
        '                      </span>\n' +
        '                    </li>';
    var pageMiddle = '';
    for(var i = 0;i<content.totalPages;i++){
        var position = i == content.pageNo?'active':'';
        pageMiddle += '<li class="'+position+'" pid="'+i+'">\n' +
            '                        <span>'+(i+1)+' <span class="sr-only">(current)</span></span>\n' +
            '                    </li>';
    }
    var pageFooter = '<li class="'+last+'" pid="'+(content.pageNo+1)+'">\n' +
        '                      <span>\n' +
        '                        <span aria-hidden="true">&raquo;</span>\n' +
        '                      </span>\n' +
        '                    </li>\n' +
        '                </ul>\n' +
        '            </nav>';

    var pageHtml = pageHeader + pageMiddle + pageFooter;

    $("#paging").empty()
    $("#paging").append(pageHtml);
}
