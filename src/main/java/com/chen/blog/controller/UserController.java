package com.chen.blog.controller;

import com.alibaba.fastjson.JSONObject;
import com.chen.blog.common.WordDefined;
import com.chen.blog.entity.Collection;
import com.chen.blog.entity.User;
import com.chen.blog.exception.BlogException;
import com.chen.blog.repository.GoodRepository;
import com.chen.blog.service.CollectionService;
import com.chen.blog.service.GoodService;
import com.chen.blog.service.UserService;
import com.chen.blog.utils.SessionUtils;
import com.chen.blog.vo.RespVo;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;




    @PostMapping("/register")
    public RespVo register(@Validated(value = {User.registerUserView.class}) User user,String token){
//        ,@NotBlank(message = "token不能为空！")
        //手动校验，后面会替换为 MD5盐值加密，如果使用jsr303，无法保存数据库
        String password = user.getPassword();
        if (password.length() < 8 || password.length() > 12) {
            throw new BlogException(WordDefined.PASSWORD_LENGTH_ERROR);
        }
        userService.register(user,token);
        return RespVo.success();
    }

    @GetMapping("/sendCode")
    public RespVo sendCode(@NotBlank(message = "电话号码不能为空") @Pattern(regexp = "1[3|4|5|7|8][0-9]\\d{8}",message = "电话号码格式不对") String phone){
        Integer expire = userService.sendCode(phone);
        return RespVo.success(expire,null);
    }


    @PostMapping("/checkCode")
    public RespVo checkCode(@NotBlank(message = "电话号码不能为空") @Pattern(regexp = "1[3|4|5|7|8][0-9]\\d{8}",message = "电话号码格式不对") String phone,@NotBlank String code){
        String token = userService.checkCode(phone, code);
        return RespVo.success(token,null);
    }


    @GetMapping("/checkPhoneUnique")
    public RespVo checkPhoneUnique(@NotBlank(message = "电话号码不能为空") @Pattern(regexp = "1[3|4|5|7|8][0-9]\\d{8}",message = "电话号码格式不对") String phone){
        boolean unique = userService.checkPhoneUnique(phone);
        return RespVo.success(unique,null);
    }
    @GetMapping("/checkHasPhone")
    public RespVo checkHasPhone(@NotBlank(message = "电话号码不能为空") @Pattern(regexp = "1[3|4|5|7|8][0-9]\\d{8}",message = "电话号码格式不对") String phone){
        boolean hasPhone = !userService.checkPhoneUnique(phone);
        return RespVo.success(hasPhone,null);
    }

    @JsonView(User.HotUserView.class)
    @GetMapping("/hot")
    public RespVo getNameAndIdList(@PageableDefault(sort = {"goodSum","viewSum","commentSum"}, direction = Sort.Direction.DESC, page = 0, size = 10)Pageable pageable){
        Page<User> userList = userService.getList(pageable);
        return RespVo.success(userList,null);
    }

    @JsonView(User.SearchUserView.class)
    @GetMapping("/users")
    public RespVo getList(@PageableDefault(sort = {"goodSum","viewSum","commentSum"}, direction = Sort.Direction.DESC, page = 0, size = 12)Pageable pageable,String nickname){
        Page<User> userList = userService.getListLikeNickname(pageable,nickname);
        return RespVo.success(userList,null);
    }

    @JsonView(User.HomeUserView.class)
    @GetMapping("/users/{id}")
    public RespVo getById(@PathVariable(value = "id")@NotNull Long id){
        User user = userService.getById(id);
        return RespVo.success(user,null);
    }


    /**
     * 获取登陆信息
     * @return
     */
    @JsonView(User.HomeUserView.class)
    @GetMapping("/loginInfo")
    public RespVo getLoginInfo(){
        User user = SessionUtils.getUser();
        return RespVo.success(user,null);
    }

    /**
     * 获取个人中心信息
     *
     * @return
     */
    @JsonView(User.UserDetailView.class)
    @GetMapping("/getUserDetail")
    public RespVo getUserDetail(){
        User user = userService.getUserDetail();
        return RespVo.success(user,null);
    }

    /**
     * 修改用户信息
     *
     * @return
     */
    @PutMapping("/update")
    public RespVo updateUserInfo(@RequestParam(value = "imgBase64",required = false)String imgBase64,@RequestParam(value = "imgType",required = false)String imgType,@NotBlank(message = "昵称不能为空！") String nickname,String briefIntr,@NotBlank(message = "博客名称不能为空！") String blogName,String description){
        userService.updateUserInfo(imgBase64,imgType,nickname,briefIntr,blogName,description);
        return RespVo.success(null,null);
    }


    @PostMapping("/uploadImg")
    public RespVo uploadImg(@RequestParam(value = "file",required = true) MultipartFile file){
        String path = userService.uploadImg(file);
        return RespVo.success(path,null);
    }



    //需要登录
    @GetMapping("/articleInfo/{articleId}")
    public RespVo getCollectionAndGood(@PathVariable("articleId") @NotNull Long articleId){
        JSONObject json = userService.getCollectionAndGood(articleId);
        return RespVo.success(json,null);
    }


    @PutMapping("/pwd")
    public RespVo updatePwd(@Validated(value = {User.UpdatePwd.class}) User user,@NotBlank(message = "token不能为空！")String token){
        //手动校验，后面会替换为 MD5盐值加密，如果使用jsr303，无法保存数据库
        String password = user.getPassword();
        if (password.length() < 8 || password.length() > 12) {
            throw new BlogException(WordDefined.PASSWORD_LENGTH_ERROR);
        }
        userService.updatePwd(user.getPhone(),password,token);
        return RespVo.success();
    }

    //需要登录
    @PutMapping("/phone")
    public RespVo updatePhone(@NotBlank(message = "电话号码不能为空")@Pattern(regexp = "1[3|4|5|7|8][0-9]\\d{8}",message = "电话号码格式不对")String phone,@NotBlank(message = "token不能为空！")String token){
        userService.updatePhone(phone,token);
        return RespVo.success();
    }


}
