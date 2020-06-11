package com.chen.blog.common;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 响应状态信息枚举类
 */
@AllArgsConstructor
@NoArgsConstructor
public enum CodeEnum {

    FAIL("0000","请求失败"),SUCCESS("0001","请求成功"),
    ABNORMAL("0002","请求异常"),PARAMETER_ERROR("0003","参数错误"),
    LOGIN_NO("0004","未登陆"),LOGIN_SUCCESS("0005","登陆成功！"),
    LOGIN_FAIL("0006","登陆失败！"), LOGOUT_FAIL("0007","注销失败！"),
    LOGOUT_SUCCESS("0008","注销成功！");


    /**
     * 状态码
     */
    private String code;
    /**
     * 状态信息
     */
    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
