package com.chen.blog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户实体
 *
 * 昵称可以重复
 *
 */
@Table(name="user")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })//解决转换异常
public class User implements Serializable {

    public interface BaseUserInfo{}//文章列表视图

    public interface registerUserView{}//暂时没用

    public interface HotUserView{}//只用 name 和 id

    public interface SearchUserView{}//查询视图

    public interface HomeUserView{}//导航栏个人信息视图

    public interface UserDetailView extends HomeUserView{}//个人中心视图

    public interface UserUpdateView{}

    public interface UpdatePwd{}


    //@NotNull //(groups = {Comment.AddCommentView.class},message = "用户id不能为空！")
    @JsonView({BaseUserInfo.class,HotUserView.class,SearchUserView.class,HomeUserView.class})
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增长策略
    @Id
    private Long id;

    @JsonView({UserDetailView.class})
    @NotBlank(message = "登录账号")
    @Size(min = 8,max = 12,message = "账号长度应在{min}-{max}")
    @Column(nullable = false,unique = true,length = 12)
    private String account;


    /**
     * MD5 盐值加密
     */
    @NotBlank(message = "密码不能为空",groups = {registerUserView.class,UpdatePwd.class})
    @Column(nullable = false)
    private String password;

    @JsonView({UserDetailView.class})
    @NotBlank(message = "电话号码不能为空",groups = {registerUserView.class,UpdatePwd.class})
    @Pattern(regexp = "1[3|4|5|7|8][0-9]\\d{8}",message = "电话号码格式不对")
    @Column(nullable = false,unique = true,length = 11)
    private String phone;


    //0 私密 1男 2女
//    @NotNull(message = "性别不能为空")
    @Column(nullable = false,columnDefinition = "int(1) default 0")
    private Integer sex;

    @JsonSerialize(using = LocalDateSerializer.class)//解决page里的LocalDateTime序列化不成功问题
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")//如果不加，转换为json后变为数组形式
//    @NotNull(message = "出生日期不能为空")
//    @Temporal(TemporalType.DATE)
    @Column
    private LocalDate birthday;//创建的时候还是使用LocalDateTime


    @Email(message = "邮箱格式不正确")
    @Column(unique = true)
    private String email;

    @JsonView({UserDetailView.class,SearchUserView.class})
    @Column
    private String briefIntr;

    @JsonView({BaseUserInfo.class,SearchUserView.class,HomeUserView.class,HotUserView.class})
    @NotBlank(message = "昵称不能为空",groups = {registerUserView.class})
    @Column(nullable = false)
    private String nickname;


    @JsonView({BaseUserInfo.class,SearchUserView.class,HomeUserView.class,HotUserView.class})
    @NotBlank(message = "头像不能为空")
    @Column(nullable = false)
    private String headurl;

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


    //0 未删除  1删除:逻辑删除
    @Column(nullable = false,name = "delete_sign",columnDefinition = "int(1) default 0")
    private Integer deleteSign;


    //0 未锁定 1锁定
    @Column(nullable = false,name="lock_sign",columnDefinition = "int(1) default 0")
    private Integer lockSign;


//    @OneToOne(targetEntity = Blog.class,cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
//    @JoinColumn(name="blog_id")
//    private Blog blog;

    @JsonView({SearchUserView.class,HomeUserView.class})
    @Column(nullable = false,name = "view_times",columnDefinition = "int default 0")
    private Integer viewSum;

    @JsonView({SearchUserView.class,HomeUserView.class})
    @Column(nullable = false,name = "good_times",columnDefinition = "int default 0")
    private Integer goodSum;

    @JsonView({SearchUserView.class,HomeUserView.class})
    @Column(nullable = false,name = "comment_times",columnDefinition = "int default 0")
    private Integer commentSum;


    @OneToMany(targetEntity = Attention.class,fetch = FetchType.LAZY,mappedBy = "user")
    private List<Attention> attentionList;
//
//    @OneToMany(targetEntity = Comment.class,fetch = FetchType.LAZY,mappedBy = "user")
//    private List<Comment> commentList;
//
//    @OneToMany(targetEntity = Good.class,fetch = FetchType.LAZY,mappedBy = "user")
//    private List<Good> goodList;


    public User(Long id,String account,String phone, String briefIntr,String nickname,String headurl) {
        this.id = id;
        this.account = account;
        this.phone = phone;
        this.briefIntr = briefIntr;
        this.nickname = nickname;
        this.headurl = headurl;
    }

//   --------------- 非数据库字段 -----------------

    @NotBlank(message = "博客名称不能为空！")
    @JsonView({UserDetailView.class})
    @Transient
    private String blogName;


    @JsonView({UserDetailView.class})
    @Transient
    private String description;

}
