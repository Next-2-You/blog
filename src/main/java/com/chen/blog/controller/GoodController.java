package com.chen.blog.controller;

import com.chen.blog.entity.Article;
import com.chen.blog.entity.Collection;
import com.chen.blog.entity.Good;
import com.chen.blog.entity.User;
import com.chen.blog.repository.GoodRepository;
import com.chen.blog.service.ArticleService;
import com.chen.blog.service.GoodService;
import com.chen.blog.utils.OthersUtils;
import com.chen.blog.utils.SessionUtils;
import com.chen.blog.vo.RespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/good")
@Validated
public class GoodController {

    @Autowired
    private GoodService goodService;
    //已经登录
    @PostMapping("/add/{id}")
    public RespVo addGood(@PathVariable(value = "id") @NotNull(message = "文章id不能为空！") Long articleId) {
        goodService.addGood(articleId);
        return RespVo.success(null, null);
    }

    //已经登录
    @DeleteMapping("/delete/{id}")
    public RespVo delete(@PathVariable(value = "id") @NotNull(message = "文章id不能为空！") Long articleId) {
        goodService.delete(articleId);
        return RespVo.success(null, null);
    }



}
