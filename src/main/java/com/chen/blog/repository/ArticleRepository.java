package com.chen.blog.repository;

import com.chen.blog.entity.Article;
import com.chen.blog.entity.Blog;
import com.chen.blog.entity.Sort;
import com.chen.blog.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;


public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findAllByType(Integer type, Pageable pageable);

    Page<Article> findAllByTypeAndCreatetimeGreaterThan(Integer type, LocalDateTime limitTime, Pageable pageable);

    Page<Article> findAllByTypeAndCreatetimeGreaterThanAndTitleLike(Integer type, LocalDateTime limitTime, String title, Pageable pageable);

    Page<Article> findAllByTypeAndTitleLike(Integer type, String title, Pageable pageable);

    Page<Article> findAllBySort(Sort sort, Pageable pageable);


    Page<Article> findAllBySortAndType(Sort sort, Integer type, Pageable pageable);

    int countById(Long articleId);


    Page<Article> findAllByTypeAndUser(Integer type, User user, Pageable pageable);

    Page<Article> findAllByUser(User user, Pageable pageable);


    @Modifying
    @Query("update Article a set a.overhead = :overhead,a.overheadTime = :overheadTime where a.id = :articleId")
    int updateOverheadAndOverheadTime(@Param(value = "overhead") Integer overhead, @Param(value = "overheadTime") LocalDateTime overheadTime, @Param(value = "articleId") Long articleId);


    @Modifying
    @Query("update Article a set a.type = :type where a.id = :articleId")
    int updateType(@Param(value = "type") Integer type, @Param(value = "articleId") Long articleId);

    //删除中间表使用原生sql
    @Modifying
    @Query(nativeQuery = true, value = "delete from join_tag_article where article_id = :articleId")
    void deleteArticleTag(@Param("articleId") Long articleId);

    //添加中间表使用原生的sql
    @Modifying
    @Query(nativeQuery = true, value = "insert into join_tag_article(tag_id,article_id) value(:tagId,:articleId)")
    void insertArticleTag(Integer tagId, Long articleId);


    @Modifying
    @Query("update Article a set a.goodTimes = (a.goodTimes-1) where  a.id = :articleId and a.goodTimes>0")
    void countDownGoodTimes(@Param(value = "articleId") Long articleId);

    @Modifying
    @Query("update Article a set a.goodTimes = (a.goodTimes+1) where  a.id = :articleId")
    void increGoodTimes(@Param(value = "articleId") Long articleId);

    @Modifying
    @Query(nativeQuery = true,value = "update article a set a.sort_id = :newSortId where a.sort_id = :sortId")
    int updateSortId(@Param(value = "newSortId") Integer newSortId,@Param(value = "sortId") Integer sortId);

    @Modifying
    @Query("update Article a set a.viewTimes = (a.viewTimes+1) where  a.id = :articleId")
    void increViewTimes(@Param(value = "articleId") Long articleId);

    @Modifying
    @Query("update Article a set a.commentTimes = (a.commentTimes+1) where  a.id = :articleId")
    void increCommentTimes(@Param(value = "articleId") Long articleId);
}
