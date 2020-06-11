package com.chen.blog.service;

import com.chen.blog.common.WordDefined;
import com.chen.blog.entity.Article;
import com.chen.blog.entity.Blog;
import com.chen.blog.entity.Sort;
import com.chen.blog.entity.User;
import com.chen.blog.exception.BlogException;
import com.chen.blog.repository.ArticleRepository;
import com.chen.blog.repository.BlogRepository;
import com.chen.blog.repository.SortRepository;
import com.chen.blog.utils.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SortService {

    @Autowired
    private SortRepository sortRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private ArticleRepository articleRepository;


    public List<Sort> getList(Long blogId) {
        Blog blog = getBlog(blogId);
        return sortRepository.findByBlog(blog);
    }

    //需要登录
    public List<Sort> getList() {
        User user = SessionUtils.getUser();
        Blog blog = getBlog(user);
        return sortRepository.findByBlog(blog);
    }

    //需要登录
    public Page<Sort> getList(Pageable pageable) {
        User user = SessionUtils.getUser();
        Blog blog = getBlog(user);
        return sortRepository.findByBlog(blog, pageable);
    }

    private Blog getBlog(Long blogId) {
        Optional<Blog> optionalBlog = blogRepository.findById(blogId);
        return getBlog(optionalBlog);
    }

    public Blog getBlog(User user) {
        Optional<Blog> optionalBlog = blogRepository.findByUser(user);
        return getBlog(optionalBlog);
    }

    //存在则返回，不存在或已经删除则抛出异常
    private Blog getBlog(Optional<Blog> optionalBlog) {
        optionalBlog.orElseThrow(() -> new BlogException(WordDefined.BLOG_NOT_FOUNT));
        Blog blog = optionalBlog.get();
        if (blog.getDeleteSign() == WordDefined.DELETE) {
            throw new BlogException(WordDefined.BLOG_ALREADY_DELETE);
        }
        return blog;
    }

    //需要登录
    @Transactional
    public void add(Sort sort) {
        User user = SessionUtils.getUser();
        Blog blog = getBlog(user);
        hasExist(blog, sort);
        sort.setBlog(blog);
        sortRepository.save(sort);
    }

    private void hasExist(Blog blog, Sort sort) {
        Sort tempSort = sortRepository.findBySortNameAndBlog(sort.getSortName(), blog);
        if (tempSort != null && tempSort.getId() != sort.getId()) {
            throw new BlogException(WordDefined.SORT_ALREADY_EXIST);
        }
    }

    @Transactional
    public void delete(Integer sortId) {
        Sort sort = hasAccess(sortId);
        //引用的文章分类置空
        int i = articleRepository.updateSortId(null, sortId);
        //删除
        sortRepository.delete(sort);
    }

    public Sort getById(Integer sortId) {
        Optional<Sort> optionalSort = sortRepository.findById(sortId);
        //不存在
        optionalSort.orElseThrow(() -> new BlogException(WordDefined.SORT_NOT_FOUNT));
        return optionalSort.get();
    }

    public Sort hasAccess(Integer sortId) {
        Sort sort = getById(sortId);
        User user = SessionUtils.getUser();
        Long userId = sort.getBlog().getUser().getId();
        if (user.getId().equals(userId)) {
            return sort;
        }
        throw new BlogException(WordDefined.NO_ACCESS);
    }

    @Transactional
    public void update(Integer sortId, String sortName) {
        User user = SessionUtils.getUser();
        Blog blog = getBlog(user);
        Sort tempSort = new Sort();
        tempSort.setSortName(sortName);
        hasExist(blog,tempSort);
        Sort sort = hasAccess(sortId);
        sort.setSortName(sortName);
        sortRepository.save(sort);
    }
}
