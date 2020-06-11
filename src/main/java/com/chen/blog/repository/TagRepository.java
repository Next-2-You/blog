package com.chen.blog.repository;

import com.chen.blog.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface TagRepository extends JpaRepository<Tag,Integer> {

    @Modifying
    @Query(nativeQuery = true,value = "update tag set num = (num-1) where id in(:tagIdList)")
    void updateTagCountDownOne(List<Integer> tagIdList);

    @Modifying
    @Query(nativeQuery = true,value = "update tag set num = (num+1) where id in(:tagIdList)")
    void updateTagAddOne(List<Integer> tagIdList);

    List<Tag> findAllByTagNameIn(List<String> nameList);
}
