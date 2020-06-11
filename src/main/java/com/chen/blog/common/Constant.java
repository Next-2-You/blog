package com.chen.blog.common;

public class Constant {

    public static String connectBlogCodePhone(String phone){
        return WordDefined.BLOG_CODE_PHONE + phone;
    }

    public static String connectBlogCodePhoneCount(String phone){
        return WordDefined.BLOG_CODE_PHONE_COUNT + phone;
    }

    public static String connectBlogTokenCode(String phone){
        return WordDefined.BLOG_TOKEN_CODE + phone;
    }

    public static String connectPassword(String password){
        return WordDefined.PSWD_SALT + password;
    }
    public static String connectRememberToken(String token){
        return WordDefined.BLOG_TOKEN_REMEMBER + token;
    }
}
