package com.chen.blog.service;


import com.chen.blog.entity.Tag;
import com.chen.blog.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public Page<Tag> getList(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

}
