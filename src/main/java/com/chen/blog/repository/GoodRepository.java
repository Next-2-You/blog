package com.chen.blog.repository;

import com.chen.blog.entity.Article;
import com.chen.blog.entity.Good;
import com.chen.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface GoodRepository extends JpaRepository<Good,Integer> {

    Good findAllByArticleAndUser(Article article, User user);

    void deleteAllByArticleAndUser(Article article,User user);
}
