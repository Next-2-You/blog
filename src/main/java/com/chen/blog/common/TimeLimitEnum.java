package com.chen.blog.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 时间限制枚举类
 */
@AllArgsConstructor
@NoArgsConstructor
public enum TimeLimitEnum {

    NOLIMIT("0","时间不限"),ONEDAY("1","最近一天"),
    ABNORMAL("2","最近一周"),PARAMETER_ERROR("3","最近三个月");


    /**
     * 状态码
     */
    private String type;
    /**
     * 状态信息
     */
    private String content;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
