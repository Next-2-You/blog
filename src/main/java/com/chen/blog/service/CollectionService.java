package com.chen.blog.service;

import com.chen.blog.common.WordDefined;
import com.chen.blog.entity.Article;
import com.chen.blog.entity.Blog;
import com.chen.blog.entity.Collection;
import com.chen.blog.entity.User;
import com.chen.blog.exception.BlogException;
import com.chen.blog.repository.CollectionRepository;
import com.chen.blog.utils.OthersUtils;
import com.chen.blog.utils.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class CollectionService {

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private ArticleService articleService;

    @Transactional
    public void addCollection(Long articleId) {
        Article article = articleService.getById(articleId);
        User user = SessionUtils.getUser();
        Collection collection = combinData(user, article);
        LocalDateTime createTime = OthersUtils.getCreateTime();
        collection.setCreatetime(createTime);
        collectionRepository.save(collection);
    }

    @Transactional
    public void delete(Integer id) {
        Collection collection = getById(id);
        collectionRepository.delete(collection);
    }

    private Collection getById(Integer id){
        Optional<Collection> optionalCollection = collectionRepository.findById(id);
        optionalCollection.orElseThrow(()->new BlogException(WordDefined.COLLECTION_NOT_FOUNT));
        Collection collection = optionalCollection.get();
        User user = SessionUtils.getUser();
        if (collection.getUser().getId().equals(user.getId())) {
            return collection;
        }
        throw new BlogException(WordDefined.NO_ACCESS);
    }



    private Collection combinData(User user, Article article) {
        Collection collection = new Collection();
        collection.setArticle(article);
        collection.setUser(user);
        return collection;
    }

    public Page<Collection> getList(Pageable pageable) {
        User user = SessionUtils.getUser();
        return collectionRepository.findAllByCondition(user.getId(),WordDefined.ARTICLE_OPEN,pageable);
    }

    @Transactional
    public void deleteByArticleId(Long articleId) {
        Article article = articleService.getById(articleId);
        User user = SessionUtils.getUser();
        collectionRepository.deleteAllByArticleAndUser(article, user);
    }
}
