package com.chen.blog.controller;

import com.chen.blog.entity.Article;
import com.chen.blog.entity.Sort;
import com.chen.blog.service.SortService;
import com.chen.blog.vo.RespVo;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/sort")
@Validated
public class SortController {

    @Autowired
    private SortService sortService;


    @GetMapping("/sorts")
    @JsonView({Sort.SortInfoView.class})
    public RespVo getList(@NotNull Long blogId){
        List<Sort> sorts = sortService.getList(blogId);
        return RespVo.success(sorts,null);
    }



//    @GetMapping("/article/{id}")
//    @ResponseBody
//    @JsonView({Sort.SortInfoView.class})
//    public RespVo getArticleListBySortId(@PathVariable("id") @NotNull Long id){
//        sortService.getById(id);
//
//
//        Article article = articleService.getById(id);
//        return RespVo.success(article,null);
//    }

    //需要登录
    @PostMapping("/add")
    public RespVo add(@Validated(value = {Sort.AddSortView.class}) Sort sort){
        sortService.add(sort);
        return RespVo.success(null,null);
    }

    //需要登录
    @GetMapping("/user")
    @JsonView({Sort.SortInfoView.class})
    public RespVo getUserSortList(@PageableDefault(sort = {"id"}, direction = org.springframework.data.domain.Sort.Direction.DESC, page = 0, size = 10) Pageable pageable){
        Page<Sort> sorts = sortService.getList(pageable);
        return RespVo.success(sorts,null);
    }

    //需要登录
    @DeleteMapping("/delete/{id}")
    public RespVo delete(@PathVariable(value = "id") @NotNull(message = "分类id不能为空！") Integer sortId){
        sortService.delete(sortId);
        return RespVo.success(null,null);
    }

    //需要登录
    @PutMapping("/update")
    public RespVo update(@NotNull(message = "分类id不能为空！") Integer sortId,@NotBlank(message = "分类名不能为空!") String sortName){
        sortService.update(sortId,sortName);
        return RespVo.success(null,null);
    }

    //需要登录
    @GetMapping("/choice")
    @JsonView({Sort.SortInfoView.class})
    public RespVo getUserSortList(){
        List<Sort> sorts = sortService.getList();
        return RespVo.success(sorts,null);
    }
}
