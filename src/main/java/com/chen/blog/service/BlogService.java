package com.chen.blog.service;

import com.chen.blog.common.WordDefined;
import com.chen.blog.entity.Blog;
import com.chen.blog.entity.User;
import com.chen.blog.exception.BlogException;
import com.chen.blog.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;


    public Blog getById(Long id) {
        User user = new User();
        user.setId(id);
        Optional<Blog> blogOptional = blogRepository.findByUser(user);
        if (blogOptional.isPresent()) {
            return blogOptional.get();
        }
        throw new BlogException(WordDefined.BLOG_NOT_FOUNT);
    }



}
