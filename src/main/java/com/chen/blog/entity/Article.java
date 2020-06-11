package com.chen.blog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Table(name="article")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })//解决转换异常
public class Article {

    public interface BaseArticleInfo{}//文章列表视图

    public interface DetailsArticleView{}

    public interface RecentUpdatesView{}

    public interface OverheadView{}

    public interface HotListView{}

    public interface AddArticleView{}

    public interface UpdateArticleView extends AddArticleView{}

    @NotNull(groups = {UpdateArticleView.class,Comment.AddCommentView.class},message = "文章id不能为空")
    @JsonView({BaseArticleInfo.class,DetailsArticleView.class,RecentUpdatesView.class,HotListView.class})
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增长策略
    @Id
    private Long id;

    @JsonView({BaseArticleInfo.class,DetailsArticleView.class,RecentUpdatesView.class,HotListView.class})
    @NotBlank(groups = {AddArticleView.class},message = "标题不能为空")
    @Column(nullable = false)
    private String title;

    @JsonView({BaseArticleInfo.class,DetailsArticleView.class})
    @NotBlank(groups = {AddArticleView.class},message = "内容不能为空")
    @Column(nullable = false)
    @Lob//text
    private String content;

    //0公开 1私有
    @JsonView({DetailsArticleView.class})
    @NotNull(groups = {AddArticleView.class},message = "是否公开属性不能为空！")
    @Column(nullable = false,columnDefinition = "int(1) default 0")
    private Integer type;

    @JsonSerialize(using = LocalDateTimeSerializer.class)//解决page里的LocalDateTime序列化不成功问题
    @JsonView({DetailsArticleView.class,BaseArticleInfo.class,RecentUpdatesView.class})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")//如果不加，转换为json后变为数组形式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
//    @org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
    private LocalDateTime createtime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)//解决page里的LocalDateTime序列化不成功问题
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")//如果不加，转换为json后变为数组形式
//    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private LocalDateTime updatetime;

    @JsonView({BaseArticleInfo.class,DetailsArticleView.class})
    @Column(nullable = false,name = "view_times",columnDefinition = "int default 0")
    private Integer viewTimes;

    @JsonView({BaseArticleInfo.class,DetailsArticleView.class})
    @Column(nullable = false,name = "good_times",columnDefinition = "int default 0")
    private Integer goodTimes;

    @JsonView({BaseArticleInfo.class,DetailsArticleView.class})
    @Column(nullable = false,name = "comment_times",columnDefinition = "int default 0")
    private Integer commentTimes;

    @NotNull(groups = {AddArticleView.class},message = "是否顶置属性不能为空！")
    @JsonView({OverheadView.class,DetailsArticleView.class})
    //0未顶置 1顶置
    @Column(nullable = false,columnDefinition = "int default 0")
    private Integer overhead;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "overhead_time")
    private LocalDateTime overheadTime;


    @ManyToOne(targetEntity = Blog.class,fetch = FetchType.LAZY)
    @JoinColumn(name="blog_id",foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))//不生成外键
    private Blog blog;

    @JsonView({User.BaseUserInfo.class,User.HotUserView.class})
    @ManyToOne(targetEntity = User.class,fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))//不生成外键
    private User user;

    @JsonView({Sort.ArticleSortView.class})
    @ManyToOne(targetEntity = Sort.class,fetch = FetchType.LAZY)
    @JoinColumn(name="sort_id",foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))//不生成外键
    private Sort sort;


    @JsonView({Tag.ArticleTagView.class})
    @ManyToMany(mappedBy = "articleList")
    private List<Tag> tagList;

    @JsonView({Comment.ArticleCommentView.class})
    @OneToMany(targetEntity = Comment.class,fetch = FetchType.LAZY,mappedBy = "article")
//    @Transient//不关联
    private List<Comment> commentList;
}
