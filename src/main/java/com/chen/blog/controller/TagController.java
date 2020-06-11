package com.chen.blog.controller;

import com.chen.blog.common.WordDefined;
import com.chen.blog.entity.Tag;
import com.chen.blog.entity.User;
import com.chen.blog.exception.BlogException;
import com.chen.blog.service.TagService;
import com.chen.blog.service.UserService;
import com.chen.blog.vo.RespVo;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@RestController
@RequestMapping("/tag")
@Validated
public class TagController {

    @Autowired
    private TagService tagService;

    @JsonView(Tag.HotTagView.class)
    @GetMapping("/hot")
    public RespVo getNameAndIdList(@PageableDefault(sort = {"num"}, direction = Sort.Direction.DESC, page = 0, size = 10)Pageable pageable){
        Page<Tag> tagList = tagService.getList(pageable);
        return RespVo.success(tagList,null);
    }


}
