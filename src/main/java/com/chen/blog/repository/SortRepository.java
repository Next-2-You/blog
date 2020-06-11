package com.chen.blog.repository;

import com.chen.blog.entity.Blog;

import com.chen.blog.entity.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SortRepository extends JpaRepository<Sort,Integer> {

    List<Sort> findByBlog(Blog blog);

    Page<Sort> findByBlog(Blog blog, Pageable pageable);

    Sort findBySortNameAndBlog(String sortName,Blog blog);

}
