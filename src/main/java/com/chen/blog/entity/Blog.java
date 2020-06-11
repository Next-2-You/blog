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
import java.time.LocalDateTime;
import java.util.List;

/**
 * 博客名称可以重复
 *
 */
@Table(name="blog")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })//解决转换异常
public class Blog {

    public interface BlogInfoView{}

    @JsonView({BlogInfoView.class})
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增长策略
    @Id
    private Long id;

    @JsonView({BlogInfoView.class})
    @NotBlank(message = "博客名称不能为空")
    @Column(name = "blog_name")
    private String blogName;

    @JsonView({BlogInfoView.class})
    @Column
    private String description;


    //0 未删除  1删除:逻辑删除
    @Column(nullable = false,name = "delete_sign",columnDefinition = "int(1) default 0")
    private Integer deleteSign;

    @JsonSerialize(using = LocalDateTimeSerializer.class)//解决page里的LocalDateTime序列化不成功问题
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")//如果不加，转换为json后变为数组形式
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

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY,targetEntity = Article.class,mappedBy = "blog")
    private List<Article> articleList;

    @OneToOne(targetEntity = User.class,cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))//不生成外键
    private User user;

}
