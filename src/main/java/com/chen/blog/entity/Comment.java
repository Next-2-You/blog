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
@Table(name="comment")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })//解决转换异常
public class Comment {

    public interface ArticleCommentView{}

    public interface ReplyCommentView{}

    public interface AddCommentView{}

    @JsonView({ArticleCommentView.class,ReplyCommentView.class})
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增长策略
    @Id
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="article_id",foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))//不生成外键
    private Article article;

    @JsonView({ArticleCommentView.class,ReplyCommentView.class})
    @ManyToOne  //(fetch = FetchType.LAZY)每次取评论的时候肯定有评论的人的信息，不用懒加载
    @JoinColumn(name="user_id",foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))//不生成外键
    private User user;

    @NotBlank(groups = {AddCommentView.class},message = "文章内容不能为空！")
    @JsonView({ArticleCommentView.class,ReplyCommentView.class})
    @NotBlank
    @Column
    private String reply;

    @JsonSerialize(using = LocalDateTimeSerializer.class)//解决page里的LocalDateTime序列化不成功问题
    @JsonView({ArticleCommentView.class,ReplyCommentView.class})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")//如果不加，转换为json后变为数组形式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
//    @org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
    private LocalDateTime createtime;


//    @JsonView({ArticleCommentView.class})
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="c_id")

    //评论的直接父级
    //注意这里是cid而不是cId,不然会报错
    @Column(nullable = false,name = "c_id",columnDefinition = "int(255) default 0")
    @NotNull(groups = {AddCommentView.class},message = "cid不能为空！")
    private Integer cid;

    //评论的顶级父级
    //注意这里是tid而不是tId,不然会报错
    @JsonView({ArticleCommentView.class,ReplyCommentView.class})
    @Column(nullable = false,name = "t_id",columnDefinition = "int(255) default 0")
    @NotNull(groups = {AddCommentView.class},message = "tid不能为空！")
    private Integer tid;


//    --------------非数据库字段----------------

    @JsonView({ReplyCommentView.class})
    @Transient
    private String nickname;

    @JsonView({ArticleCommentView.class})
    @Transient
    private Integer replyCount;


}
