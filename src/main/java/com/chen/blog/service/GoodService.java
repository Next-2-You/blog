package com.chen.blog.service;

import com.chen.blog.entity.Article;
import com.chen.blog.entity.Good;
import com.chen.blog.entity.User;
import com.chen.blog.repository.ArticleRepository;
import com.chen.blog.repository.GoodRepository;
import com.chen.blog.repository.UserRepository;
import com.chen.blog.utils.OthersUtils;
import com.chen.blog.utils.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class GoodService {


    @Autowired
    private GoodRepository goodRepository;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public void addGood(Long articleId) {
        Article article = articleService.getById(articleId);
        User user = SessionUtils.getUser();
        Good good = combinData(article,user);
        LocalDateTime createTime = OthersUtils.getCreateTime();
        good.setCreatetime(createTime);
        goodRepository.save(good);

        userRepository.increGoodSum(article.getUser().getId());
        articleRepository.increGoodTimes(articleId);
    }

    @Transactional
    public void delete(Long articleId) {
        Article article = articleService.getById(articleId);
        User user = SessionUtils.getUser();
        goodRepository.deleteAllByArticleAndUser(article,user);

        userRepository.countDownGoodSum(article.getUser().getId());
        articleRepository.countDownGoodTimes(articleId);
    }

    private Good combinData(Article article,User user){
        Good good = new Good();
        good.setArticle(article);
        good.setUser(user);
        return good;
    }

}
