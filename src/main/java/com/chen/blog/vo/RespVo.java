package com.chen.blog.vo;

import com.chen.blog.common.CodeEnum;
import com.chen.blog.entity.*;
import com.chen.blog.utils.OthersUtils;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 响应实体
 * @param <T>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonView({User.BaseUserInfo.class, Article.BaseArticleInfo.class,
        User.HotUserView.class,Tag.HotTagView.class,User.SearchUserView.class,User.HomeUserView.class,
        Vo.ArticleDetailsNoCommentView.class, Comment.ArticleCommentView.class,
        Sort.SortInfoView.class,Article.RecentUpdatesView.class,Article.HotListView.class,Article.DetailsArticleView.class,Blog.BlogInfoView.class})//这里的接口要是方法上接口的父类或相同
public class RespVo<T> {

    /**
     * 状态码
     */
    private String code;

    /**
     * 状态信息
     */
    private String msg;
    /**
     * 内容
     */

    private T content;

    /**
     * 扩展信息
     */
    private Map<String,Object> ext;

    /**
     * 时间
     */
    private String respTime = LocalDateTime.now().format(OthersUtils.formatterWithTime);

    public RespVo(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public RespVo(String code, String msg, T content, Map<String, Object> ext) {
        this.code = code;
        this.msg = msg;
        this.content = content;
        this.ext = ext;
    }

    /**
     * 快速成功回复
     * @return
     */
    public static RespVo success(){
        return new RespVo(CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getMsg());
    }

    public static <T> RespVo<T> success(T content, Map<String, Object> ext){
        return new RespVo<T>(CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getMsg(),content,ext);
    }

    /**
     * 快速失败回复
     * @return
     */
    public static RespVo fail(){
        return new RespVo(CodeEnum.FAIL.getCode(),CodeEnum.FAIL.getMsg());
    }

    public static <T> RespVo<T> fail(T content, Map<String, Object> ext){
        return new RespVo<T>(CodeEnum.FAIL.getCode(),CodeEnum.FAIL.getMsg(),content,ext);
    }

    /**
     * 通用回复
     *
     * @param code
     * @param msg
     * @param content
     * @param ext
     * @param <T>
     * @return
     */
    public static <T> RespVo<T> general(String code,String msg,T content,Map<String,Object> ext){
        return new RespVo<T>(code,msg,content,ext);
    }

    public static <T> RespVo<T> general(CodeEnum codeEnum,T content,Map<String,Object> ext){
        return new RespVo<T>(codeEnum.getCode(),codeEnum.getMsg(),content,ext);
    }


}
