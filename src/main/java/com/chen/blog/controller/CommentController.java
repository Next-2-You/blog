package com.chen.blog.controller;

import com.chen.blog.entity.*;
import com.chen.blog.exception.BlogException;
import com.chen.blog.service.CommentService;
import com.chen.blog.utils.SessionUtils;
import com.chen.blog.vo.RespVo;
import com.chen.blog.vo.Vo;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/comment")
@Validated
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 一级评论
     *
     * tid = 0;
     *
     * @param pageable
     * @param articleId
     * @param tid
     * @return
     */
    @GetMapping("/comments")
    @JsonView({Vo.CommentView.class})
    public RespVo getList(@PageableDefault(sort = "createtime", direction = Sort.Direction.ASC, page = 0, size = 4) Pageable pageable, @NotNull Long articleId,@NotNull Integer tid){
        Page<Comment> page = commentService.getList(pageable,articleId,tid);
        return RespVo.success(page,null);
    }

    /**
     * 查看回复
     *
     * tid = 一级评论的id
     *
     * @param articleId
     * @param tid
     * @return
     */
    @GetMapping("/replys")
    @JsonView({Vo.ReplyView.class})
    public RespVo getReply(@NotNull Long articleId,@NotNull Integer tid){
        List<Comment> comments = commentService.getReply(articleId, tid);
        return RespVo.success(comments,null);
    }

    /**
     * 回复
     * @param comment
     * @return
     */
    @PostMapping("/reply")
    public RespVo addReply(@Validated(value = {Comment.AddCommentView.class})Comment comment){
        commentService.addReply(comment);
        return RespVo.success(null,null);
    }




}
