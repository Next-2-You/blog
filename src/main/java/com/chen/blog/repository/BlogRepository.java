package com.chen.blog.repository;

import com.chen.blog.entity.Blog;
import com.chen.blog.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    Optional<Blog> findByUser(User user);

    @Modifying
    @Query(value = "update Blog b set b.updatetime = :updatetime,b.blogName = :blogName,b.description = :description where b.id = :blogId")
    int updateBlogInfo(@Param(value = "updatetime") LocalDateTime updatetime, @Param("blogName") String blogName, @Param("description") String description, @Param(value = "blogId") Long blogId);
}
