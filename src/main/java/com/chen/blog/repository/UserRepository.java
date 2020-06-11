package com.chen.blog.repository;

import com.chen.blog.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {

//    User findByUsername(String username);

    User findUserByPhone(String phone);

    User findUserByPhoneOrAccount(String phone, String account);

    Page<User> findAllByNicknameLike(String nickname, Pageable pageable);

    @Modifying
    @Query("update User u set u.headurl=:headurl,u.updatetime=:updatetime,u.nickname=:nickname,u.briefIntr=:briefIntr where u.id =:userId ")
    int updateUserInfo(@Param(value = "updatetime") LocalDateTime updatetime, @Param(value = "userId") Long userId, @Param(value = "nickname") String nickname, @Param("briefIntr") String briefIntr, @Param("headurl") String headurl);

    @Modifying
    @Query("update User u set u.goodSum = (u.goodSum-1) where  u.id = :articleId and u.goodSum>0")
    void countDownGoodSum(@Param(value = "articleId") Long articleId);

    @Modifying
    @Query("update User u set u.goodSum = (u.goodSum+1)where  u.id = :articleId")
    void increGoodSum(@Param(value = "articleId") Long articleId);

    @Modifying
    @Query("update User u set u.password = :password where  u.phone = :phone")
    int updatePwd(@Param(value = "password") String password,@Param(value = "phone")String phone);

    @Modifying
    @Query("update User u set u.viewSum = (u.viewSum+1)where  u.id = :userId")
    void increViewSum(@Param(value = "userId") Long userId);

    @Modifying
    @Query("update User u set u.commentSum = (u.commentSum+1)where  u.id = :userId")
    void increCommentSum(@Param(value = "userId") Long userId);

    @Modifying
    @Query("update User u set u.phone = :phone where  u.id = :id")
    int updatePhone(@Param(value = "phone") String phone,@Param(value = "id")Long id);


}
