package com.chen.blog.vo;

import com.chen.blog.entity.*;

public class Vo {

    public interface BaseUserAndArticle extends User.BaseUserInfo , Article.BaseArticleInfo{}//文章列表视图

    public interface ArticleDetailsNoCommentView extends Article.DetailsArticleView, Sort.ArticleSortView, Tag.ArticleTagView{}//文章视图不带评论

    public interface CommentView extends User.BaseUserInfo,Comment.ArticleCommentView{}//顶级评论视图

    public interface ReplyView extends  User.BaseUserInfo,Comment.ReplyCommentView{}//顶级评论下的回复视图

    public interface BaseUserAndArticleWithOverhead extends User.BaseUserInfo , Article.BaseArticleInfo,Article.OverheadView{}//文章列表视图带顶置

    public interface HotArticleView extends Article.HotListView,User.HotUserView{}

    public interface BaseArticleWithOverhead extends Article.BaseArticleInfo,Article.OverheadView{}//文章列表视图带顶置

}
