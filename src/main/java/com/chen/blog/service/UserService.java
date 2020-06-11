package com.chen.blog.service;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.chen.blog.common.Constant;
import com.chen.blog.common.WordDefined;
import com.chen.blog.entity.*;
import com.chen.blog.exception.BlogException;
import com.chen.blog.redis.RedisUtils;
import com.chen.blog.repository.BlogRepository;
import com.chen.blog.repository.CollectionRepository;
import com.chen.blog.repository.GoodRepository;
import com.chen.blog.repository.UserRepository;
import com.chen.blog.sms.SmsVerification;
import com.chen.blog.utils.*;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private DBIdGenerate dbIdGenerate;

    @Autowired
    private DBIdConfig dbIdConfig;

    @Autowired
    private BlogService blogService;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private GoodRepository goodRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void register(User user, String token) {
        //校验token
        checkToken(user.getPhone(), token);
        LocalDateTime createTime = OthersUtils.getCreateTime();
        initUser(user, createTime);
        User saveUser = userRepository.save(user);
        Blog blog = initBlog(saveUser, createTime);
        blogRepository.save(blog);
    }

    private void initUser(User user, LocalDateTime createTime) {
        user.setCreatetime(createTime);
        //分配唯一账号
        Long account = dbIdGenerate.doGetDBId(WordDefined.ACCOUNT, dbIdConfig.getAccount());
        user.setAccount(String.valueOf(account));
        //设置默认头像（可修改）
        user.setHeadurl(WordDefined.DEFAULT_HEAD_URL);
        //MD5 盐值加密
        String password = OthersUtils.MD5(Constant.connectPassword(user.getPassword()));
        user.setPassword(password);
        user.setSex(0);
        user.setViewSum(0);
        user.setGoodSum(0);
        user.setDeleteSign(0);
        user.setCommentSum(0);
        user.setLockSign(0);
        user.setBriefIntr("");
        user.setBlogName(user.getNickname());
    }

    private Blog initBlog(User user, LocalDateTime createTime) {
        Blog blog = new Blog();
        //默认博客的名称为昵称（可修改）
        blog.setBlogName(user.getNickname());
        blog.setCreatetime(createTime);
        blog.setUser(user);
        blog.setDeleteSign(0);
        blog.setDescription("");
        return blog;
    }


    public List<User> getTopList() {
        Sort sort = Sort.by("goodSum", "viewSum", "commentSum");
//        Sort.by()

        return null;
    }


    /**
     * 校验手机验证码
     *
     * @param code
     */
    public String checkCode(String phone, String code) {
        String value = redisUtils.getValue(Constant.connectBlogCodePhone(phone));
        if (value != null && value.equals(code)) {
            //移除验证码
            redisUtils.delKey(Constant.connectBlogCodePhone(phone));
            //创建token
            String token = createToken(phone);
            return token;
        }
        throw new BlogException(WordDefined.ERROR_CODE);
    }


    /**
     * 发送手机验证码
     * <p>
     * 同一个手机号一天只可以发送 5 次验证码
     * 验证码 2 分钟后失效
     * <p>
     * 返回倒计时秒数
     *
     * @param phone
     */
    public Integer sendCode(String phone) {
        Long expire = redisUtils.getExpire(Constant.connectBlogCodePhone(phone));
        //验证码还存在直接返回剩余倒计时秒数
        if (expire > 0) {
            return Integer.valueOf(expire + "");
        }
        //发送次数
        String count = redisUtils.getValue(Constant.connectBlogCodePhoneCount(phone));
        boolean sendSms = false;
        if (count == null) {
            String code = OthersUtils.createCode(WordDefined.PHOME_CODE_DIGIT);
            //发送验证码
            try {
                sendSms = SmsVerification.sendSms(phone, code);
            } catch (ClientException e) {
                log.info("发送验证码失败，原因：[{}-{}]", e.getErrCode(), e.getErrMsg());
            }
            if (!sendSms) {
                throw new BlogException(WordDefined.SEND_SMS_FAIL);
            }

            //存储redis
            //2分钟过期
            redisUtils.setKeyAndTimeOut(Constant.connectBlogCodePhone(phone), WordDefined.PHONE_CODE_TIME_OUT, code);
            //当天剩余时间
            long seconds = OthersUtils.getCurrentDateRemainSeconds();
            redisUtils.setKeyAndTimeOut(Constant.connectBlogCodePhoneCount(phone), seconds, "1");
            return WordDefined.PHONE_CODE_TIME_OUT;
        }
        if (Integer.valueOf(count) < WordDefined.MAX_PHONE_SEND_COUNT) {
            String code = OthersUtils.createCode(WordDefined.PHOME_CODE_DIGIT);
            //发送验证码
            try {
                sendSms = SmsVerification.sendSms(phone, code);
            } catch (ClientException e) {
                log.info("发送验证码失败，原因：[{}-{}]", e.getErrCode(), e.getErrMsg());
            }
            if (!sendSms) {
                throw new BlogException(WordDefined.SEND_SMS_FAIL);
            }
            //存储redis
            redisUtils.setKeyAndTimeOut(Constant.connectBlogCodePhone(phone), WordDefined.PHONE_CODE_TIME_OUT, code);
            //自增1
            redisUtils.increment(Constant.connectBlogCodePhoneCount(phone), 1);
            return WordDefined.PHONE_CODE_TIME_OUT;
        }
        //超次数
        throw new BlogException(WordDefined.PHONE_SEND_CODE_PASS_MAX_COUNT);
    }


    /**
     * 创建token
     *
     * @param phone
     * @return
     */
    public String createToken(String phone) {
        String codeToken = OthersUtils.createCodeToken();
        String key = Constant.connectBlogTokenCode(phone);
        redisUtils.setKeyAndTimeOut(key, WordDefined.PHONE_TOKEN_TIME_OUT, codeToken);
        return codeToken;
    }

    /**
     * 校验token
     *
     * @param token
     * @return
     */
    public void checkToken(String phone, String token) {
        String value = redisUtils.getValue(Constant.connectBlogTokenCode(phone));
        if (token.equals(value)) {
            //移除token
            redisUtils.delKey(Constant.connectBlogTokenCode(phone));
            return;
        }
        Long expire = redisUtils.getExpire(Constant.connectBlogTokenCode(phone));
        if (expire != null && expire <= 0) {
            throw new BlogException(WordDefined.EXPIRE_TOKEN);
        }
        throw new BlogException(WordDefined.ERROR_TOKEN);
    }


    /**
     * 检查手机号码唯一性
     *
     * @param phone
     * @return
     */
    public boolean checkPhoneUnique(String phone) {
        if (phone == null || phone.trim().equals("")) {
            return false;
        }
        User user = userRepository.findUserByPhone(phone);
        return user == null ? true : false;
    }


    public Page<User> getList(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> getListLikeNickname(Pageable pageable, String nickname) {
        if (null == nickname || nickname.trim().equals("")) {
            return userRepository.findAll(pageable);
        }
        return userRepository.findAllByNicknameLike("%" + nickname + "%", pageable);
    }

    public User getById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getDeleteSign() == WordDefined.DELETE) {
                throw new BlogException(WordDefined.USER_ALREADY_DELETE);
            }
            return user;
        }
        throw new BlogException(WordDefined.USER_NOT_FOUNT);
    }

    public User getUserDetail() {
        User user = SessionUtils.getUser();
        Blog blog = blogService.getById(user.getId());
        user.setBlogName(blog.getBlogName());
        user.setDescription(blog.getDescription());
        return user;
    }


    public String uploadImg(MultipartFile file) {


        return null;
    }

    @Transactional
    public void updateUserInfo(String imgBase64, String imgType, String nickname, String briefIntr, String blogName, String description) {
        User user = SessionUtils.getUser();
        String headurl = user.getHeadurl();
        if (OthersUtils.isBlank(imgBase64) && OthersUtils.isBlank(imgType)) {
            byte[] bytes = Base64.getDecoder().decode(imgBase64);
            String[] str = FastDFSUtil.upload(bytes, imgType);
            if (str != null) {
                headurl = str[0] + "/" + str[1];
                if (OthersUtils.isBlank(user.getHeadurl()) && !user.getHeadurl().equals(WordDefined.DEFAULT_HEAD_URL))
                    FastDFSUtil.deleteFile(user.getHeadurl());//删除图片服务器上原来的图片
            }
        }
        LocalDateTime updatetime = OthersUtils.getCreateTime();

        int i = userRepository.updateUserInfo(updatetime, user.getId(), nickname, briefIntr, headurl);
        if (i != 1) {
            throw new BlogException(WordDefined.USER_INOF_ERROR);
        }
        Blog blog = blogService.getById(user.getId());
        i = blogRepository.updateBlogInfo(updatetime, blogName, description, blog.getId());
        if (i != 1) {
            throw new BlogException(WordDefined.BLOG_INFO_ERROR);
        }
        SessionUtils.updateUserInfo(nickname, briefIntr, headurl, user.getPhone());
    }

    public JSONObject getCollectionAndGood(Long articleId) {
        Article article = new Article();
        article.setId(articleId);
        User user = SessionUtils.getUser();
        Collection collection = collectionRepository.findAllByArticleAndUser(article, user);
        Good good = goodRepository.findAllByArticleAndUser(article, user);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isCollection", collection == null ? false : true);
        jsonObject.put("isGood", good == null ? false : true);
        return jsonObject;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updatePwd(String phone, String password, String token) {
        //校验token
        checkToken(phone, token);
        //MD5 盐值加密
        password = OthersUtils.MD5(Constant.connectPassword(password));
        int i = userRepository.updatePwd(password, phone);
        if (i != 1) {
            throw new BlogException(WordDefined.PASSWORD_UPDATE_ERROR);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updatePhone(String phone, String token) {
        //校验token
        checkToken(phone, token);
        User user = SessionUtils.getUser();
        userRepository.updatePhone(phone, user.getId());
        SessionUtils.updateUserInfo(user.getNickname(), user.getBriefIntr(), user.getHeadurl(), phone);
    }
}
