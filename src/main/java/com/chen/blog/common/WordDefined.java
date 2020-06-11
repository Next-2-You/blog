package com.chen.blog.common;

/**
 * 字典
 */
public class WordDefined {

    /**
     * 密码盐
     */
    public static final String PSWD_SALT = "cycblog";

    /**
     * ACCOUNT(登录账号）
     * 只用来分配唯一账号
     */
    public static String ACCOUNT = "ACCOUNT";

    public static final String PHONE_NOT_UNIQUE = "手机号码已经被使用！";

    public static final Integer DELETE = 1;

    public static final Integer NOT_DELETE = 0;

    //默认头像
    public static final String DEFAULT_HEAD_URL = "group1/M00/00/00/wKi4eFy4HjiAXC4cAAHM4Zz6QLQ150.png";


    public static final String PHONE_SEND_CODE_PASS_MAX_COUNT = "该手机号码一天发送验证码次数超过5次！";

    public static final String ERROR_CODE = "错误验证码！";

    public static final String ERROR_TOKEN = "token错误！";

    public static final String EXPIRE_TOKEN = "token过期！";

    public static final String PASSWORD_LENGTH_ERROR = "密码长度应在 8 - 12 位";

    public static final String PASSWORD_UPDATE_ERROR = "密码修改失败！";

    public static final String USER_NOT_FOUNT = "不存在该用户！";

    public static final String USER_INOF_ERROR = "用户信息有误！";

    public static final String ARTICLE_NOT_FOUNT = "不存在该文章！";


    public static final String ARTICLE_NOT_UNIQUE = "文章不唯一！";


    public static final String NO_ACCESS = "无访问权限！";

    public static final String BLOG_NOT_FOUNT = "博客不存在！";

    public static final String BLOG_ALREADY_DELETE = "博客已删除！";

    public static final String BLOG_INFO_ERROR = "博客信息有误！";

    public static final String USER_ALREADY_DELETE = "用户已经被注销！";

    public static final String SORT_NOT_FOUNT = "分类不存在！";

    public static final String SORT_ALREADY_EXIST = "分类已经存在！";

    public static final String COLLECTION_NOT_FOUNT = "收藏不存在！";

    public static final String SEND_SMS_FAIL = "发送验证码失败，请检查手机号并稍后重试！";

    //文章公开
    public static final Integer ARTICLE_OPEN = 0;

    //私有
    public static final Integer ARTICLE_CLOSE = 1;

    //不顶置
    public static final Integer OVERHEAD_CLOSE = 0;

    //顶置
    public static final Integer OVERHEAD_OPEN = 1;

    //无分类（默认）
    public static final Integer SORT_NOT = -1;


    //手机验证码位数
    public static final Integer PHOME_CODE_DIGIT = 6;

    //	redis-key
    //手机验证码前缀
    public static final String BLOG_CODE_PHONE = "blog:code:phone:";//blog:code:phone:1355xxxxx
    //手机验证码次数
    public static final String BLOG_CODE_PHONE_COUNT = "blog:code:phone:count:";//blog:code:phone:count:1355xxxxx
    //验证码通过后token
    public static final String BLOG_TOKEN_CODE = "blog:token:code:";//blog:token:code:1355xxxxx

    public static final String BLOG_TOKEN_REMEMBER = "blog:token:remember:";//blog:token:remember:xxxxx

    //手机验证码过期时间
    public static final Integer PHONE_CODE_TIME_OUT = 60 * 2;
    //token过期时间
    public static final Integer PHONE_TOKEN_TIME_OUT = 60 * 5;


    public static final Integer MAX_PHONE_SEND_COUNT = 5;


    public static final String BASE_IMGE_URL = "http://192.168.184.120:8888/";
}
