package com.chen.blog.controller;


import com.chen.blog.entity.Blog;
import com.chen.blog.service.BlogService;
import com.chen.blog.vo.RespVo;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/blog")
@Validated
public class BlogController {

    @Autowired
    private BlogService blogService;

    @GetMapping("/get")
    public RespVo getList(Blog blog){
        try{
//            userService.register(user,token);
        }catch (Exception e){
            return RespVo.fail(e.getMessage(),null);
        }
        return RespVo.success();
    }

    @GetMapping("/getByUserId")
    @JsonView({Blog.BlogInfoView.class})
    public RespVo getByUserId(@NotNull(message = "用户id不能为空！") Long userId){
        Blog blog = blogService.getById(userId);
        return RespVo.success(blog,null);
    }


}
