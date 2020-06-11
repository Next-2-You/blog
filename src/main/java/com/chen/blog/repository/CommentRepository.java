package com.chen.blog.repository;

import com.chen.blog.entity.Article;
import com.chen.blog.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment,Integer> {


    Page<Comment> findByArticleIsAndTid(Article article, Integer tid, Pageable pageable);

    int countByTid(Integer tid);

    List<Comment> findByArticleIsAndTid(Article article, Integer tid, Sort sort);

    void deleteByArticle(Article article);

}
