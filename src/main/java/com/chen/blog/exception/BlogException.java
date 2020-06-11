package com.chen.blog.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogException extends RuntimeException{

    /**
     * 自定义异常编码
     */
    private String errCode;

    /**
     * 自定义异常数据
     */
    private Object errData;

    public BlogException(String message) {
        super(message);
    }

}
