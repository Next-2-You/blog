package com.chen.blog.service;

import com.alibaba.fastjson.JSONObject;
import com.chen.blog.common.TimeLimitEnum;
import com.chen.blog.common.WordDefined;
import com.chen.blog.entity.*;
import com.chen.blog.exception.BlogException;
import com.chen.blog.repository.*;
import com.chen.blog.utils.FastDFSUtil;
import com.chen.blog.utils.OthersUtils;
import com.chen.blog.utils.SensitivewordFilter;
import com.chen.blog.utils.SessionUtils;
import com.sun.xml.bind.v2.TODO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private SortRepository sortRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private SortService sortService;

    @Autowired
    private SensitivewordFilter sensitivewordFilter;

    public Page<Article> getList(Pageable pageable) {
        return articleRepository.findAllByType(WordDefined.ARTICLE_OPEN, pageable);
    }
    @Transactional
    public Article getById(Long id) {
        Optional<Article> optionalArticle = articleRepository.findById(id);
        //不存在
        optionalArticle.orElseThrow(() -> new BlogException(WordDefined.ARTICLE_NOT_FOUNT));
        Article article = optionalArticle.get();
        //公开 || 私有==登录的用户
        Long userId = article.getUser().getId();
        if (article.getType() == WordDefined.ARTICLE_OPEN || userId.equals(SessionUtils.getUserId())) {
            //文章和用户的阅览数+1
            viewsIncreOne(id,userId);
            article.setViewTimes(article.getViewTimes()+1);
            return article;
        }
        throw new BlogException(WordDefined.NO_ACCESS);
    }

    public Page<Article> getListBySortId(Integer sortId, Pageable pageable) {
        Optional<Sort> optionalSort = sortRepository.findById(sortId);
        optionalSort.orElseThrow(() -> new BlogException(WordDefined.SORT_NOT_FOUNT));
        Long userId = SessionUtils.getUserId();
        Sort sort = optionalSort.get();
        if (userId == -1 || !sort.getBlog().getUser().getId().equals(userId)) {
            //公开
            return articleRepository.findAllBySortAndType(sort, WordDefined.ARTICLE_OPEN, pageable);
        }
        //全部
        return articleRepository.findAllBySort(sort, pageable);
    }

    public Page<Article> getArticleListByUserId(Pageable pageable, Long userId) {
        Long loginUserId = SessionUtils.getUserId();
        User user = getUser(userId);
        //没有登录 || 登录用户 ！= 获取列表主人
        if (loginUserId == -1 || !loginUserId.equals(userId)) {
            //公开
            return articleRepository.findAllByTypeAndUser(WordDefined.ARTICLE_OPEN, user, pageable);
        }
        //全部
        return articleRepository.findAllByUser(user, pageable);
    }

    //存在则返回，不存在则抛出异常
    private User getUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        optionalUser.orElseThrow(() -> new BlogException(WordDefined.USER_NOT_FOUNT));
        User user = optionalUser.get();
        Integer deleteSign = user.getDeleteSign();
        if (deleteSign == WordDefined.DELETE) {
            throw new BlogException(WordDefined.USER_ALREADY_DELETE);
        }
        return user;
    }

    @Transactional
    public int changeOverhead(Long articleId, Integer overhead) {
        Article article = getArticle(articleId);
        Long userId = SessionUtils.getUserId();
        if (article.getUser().getId().equals(userId)) {
            LocalDateTime overheadTime = null;
            if (WordDefined.OVERHEAD_OPEN == overhead) {
                overheadTime = OthersUtils.getCreateTime();
            }
            return articleRepository.updateOverheadAndOverheadTime(overhead, overheadTime, articleId);
        }
        throw new BlogException(WordDefined.NO_ACCESS);
    }

    @Transactional
    public int changeType(Long articleId, Integer type) {
        Article article = getArticle(articleId);
        Long userId = SessionUtils.getUserId();
        if (article.getUser().getId().equals(userId)) {
            return articleRepository.updateType(type, articleId);
        }
        throw new BlogException(WordDefined.NO_ACCESS);
    }


    //存在则返回，不存在则抛出异常
    private Article getArticle(Long articleId) {
        Optional<Article> optionalArticle = articleRepository.findById(articleId);
        optionalArticle.orElseThrow(() -> new BlogException(WordDefined.ARTICLE_NOT_FOUNT));
        return optionalArticle.get();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long articleId) {
        Article article = getArticle(articleId);
        Long userId = SessionUtils.getUserId();
        if (article.getUser().getId().equals(userId)) {
            //删除评论
            commentRepository.deleteByArticle(article);
            //获取文章引用的标签
            List<Tag> tagList = article.getTagList();
            if (tagList != null && tagList.size() > 0) {
                //删除中间表的标签
                articleRepository.deleteArticleTag(articleId);
                List<Integer> tagIdList = tagList.parallelStream()
                        .map(Tag::getId)
                        .collect(Collectors.toList());
                //引用标签统计 -1
                tagRepository.updateTagCountDownOne(tagIdList);
            }
            //删除文章
            articleRepository.deleteById(articleId);
            return;
        }
        throw new BlogException(WordDefined.NO_ACCESS);
    }

    public Page<Article> getListBySearch(Pageable pageable, String title, String limitTimeType) {
        LocalDateTime localDateTime = null;
        //今天23:59:59:999..
        LocalDateTime todayMax = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        if (limitTimeType.equals(TimeLimitEnum.ONEDAY.getType())) {
            //最近一天
            localDateTime = todayMax.plusDays(-1);
        } else if (limitTimeType.equals(TimeLimitEnum.ABNORMAL.getType())) {
            //最近一周
            localDateTime = todayMax.plusWeeks(-1);
        } else if (limitTimeType.equals(TimeLimitEnum.PARAMETER_ERROR.getType())) {
            //最近一个月
            localDateTime = todayMax.plusMonths(-1);
        }
        return getListBySearch(pageable, title, localDateTime);
    }

    public Page<Article> getListBySearch(Pageable pageable, String title, LocalDateTime limitTime) {
        if (title == null || title.trim().equals("")) {
            if (limitTime == null) {
                return articleRepository.findAllByType(WordDefined.ARTICLE_OPEN, pageable);
            }
            return articleRepository.findAllByTypeAndCreatetimeGreaterThan(WordDefined.ARTICLE_OPEN, limitTime, pageable);
        }
        if (limitTime == null) {
            return articleRepository.findAllByTypeAndTitleLike(WordDefined.ARTICLE_OPEN, "%" + title + "%", pageable);
        }
        return articleRepository.findAllByTypeAndCreatetimeGreaterThanAndTitleLike(WordDefined.ARTICLE_OPEN, limitTime, "%" + title + "%", pageable);

    }

    @Transactional
    public void addArticle(Article article, String tagName, String sortId) {
        List<Tag> tagList = combinData(article, tagName, sortId);
        //html转码+敏感词过滤
        String content = article.getContent();
        String s = sensitivewordFilter.replaceSensitiveWord(HtmlUtils.htmlEscape(content), 2, "*");
        article.setContent(s);
        Article saveArticle = articleRepository.save(article);
        Long articleId = saveArticle.getId();
        //插入中间表
        for (Tag tag : tagList) {
            articleRepository.insertArticleTag(tag.getId(), articleId);
        }
    }

    @Transactional
    public List<Tag> combinData(Article article, String tagName, String sortId) {
        User user = SessionUtils.getUser();
        Blog blog = sortService.getBlog(user);
        if (sortId != null && !sortId.trim().equals(WordDefined.SORT_NOT + "")) {
            Integer sId = Integer.valueOf(sortId);
            List<Sort> sortList = sortRepository.findByBlog(blog);
            Sort saveSort = null;
            for (Sort tempSort : sortList) {
                if (tempSort.getId().equals(sId)) {
                    saveSort = tempSort;
                    article.setSort(saveSort);
                    break;
                }
            }
            if (saveSort == null) {
                throw new BlogException(WordDefined.SORT_NOT_FOUNT);
            }
        }
        List<String> tagNameList = OthersUtils.changeStrToList(tagName, String.class);
        List<Tag> tagList = tagRepository.findAllByTagNameIn(tagNameList);
        Map<String, Tag> tagMap = tagList.parallelStream()
                .collect(Collectors.toMap(Tag::getTagName, t -> t));
        List<Tag> saveTagList = new ArrayList<>();
        List<Integer> tagIdList = new ArrayList<>();
        tagNameList.parallelStream()
                .forEach(name -> {
                    Tag tag = tagMap.get(name);
                    if (tag == null) {
                        Tag saveTag = new Tag();
                        saveTag.setNum(1);
                        saveTag.setTagName(name);
                        saveTagList.add(saveTag);
                        return;
                    }
                    Integer id = tag.getId();
                    tagIdList.add(id);
                });
        //引用标签+1
        if (tagIdList.size() > 0) {
            tagRepository.updateTagAddOne(tagIdList);
        }
        //保存还没有的标签
        if (saveTagList.size() > 0) {
            List<Tag> tag = tagRepository.saveAll(saveTagList);
            tagList.addAll(tag);
        }
        LocalDateTime createTime = OthersUtils.getCreateTime();
        //顶置
        if (article.getOverhead().equals(WordDefined.OVERHEAD_OPEN)) {
            article.setOverheadTime(createTime);
        }
        initTimes(article);
        article.setUser(user);
        article.setBlog(blog);
        article.setCreatetime(createTime);
        return tagList;
    }

    private void initTimes(Article article) {
        article.setCommentTimes(0);
        article.setGoodTimes(0);
        article.setViewTimes(0);
    }

    public void checkArticleAuthor(Long articleId) {
        Optional<Article> optionalArticle = articleRepository.findById(articleId);
        //不存在
        optionalArticle.orElseThrow(() -> new BlogException(WordDefined.ARTICLE_NOT_FOUNT));
        Article article = optionalArticle.get();
        //是否是文章的拥有者
        if (!article.getUser().getId().equals(SessionUtils.getUserId())) {
            throw new BlogException(WordDefined.NO_ACCESS);
        }
    }

    @Transactional
    public void updateArticle(Article article, String tagName, String sortId) {
        Article dbArticle = getById(article.getId());
        List<Tag> tagList = dbArticle.getTagList();
        if (tagList != null && tagList.size() > 0) {
            //删除中间表的标签
            articleRepository.deleteArticleTag(dbArticle.getId());
            List<Integer> tagIdList = tagList.parallelStream()
                    .map(Tag::getId)
                    .collect(Collectors.toList());
            //引用标签统计 -1
            tagRepository.updateTagCountDownOne(tagIdList);
        }
        addArticle(article,tagName,sortId);
/*        List<Tag> saveTag = combinData(article, tagName, sortId);
        //html转码
        String content = article.getContent();
        article.setContent(HtmlUtils.htmlEscape(content));

        Article saveArticle = articleRepository.save(article);
        Long articleId = saveArticle.getId();
        //插入中间表
        for (Tag tag : saveTag) {
            articleRepository.insertArticleTag(tag.getId(), articleId);
        }*/
    }

    public Page<Article> getArticleList(Pageable pageable, Integer type) {
        User user = SessionUtils.getUser();
        if (type == null) {
            return articleRepository.findAllByUser(user, pageable);
        }
        return articleRepository.findAllByTypeAndUser(type, user, pageable);
    }

    public JSONObject uploadImage(HttpServletRequest request, MultipartFile file) {
        log.info("【FileController】 fileName={},fileOrginNmae={},fileSize={}", file.getName(), file.getOriginalFilename(), file.getSize());
        List<String[]> list = FastDFSUtil.upload(new MultipartFile[]{file});
        JSONObject json = new JSONObject();
        if (list != null && list.size() > 0) {
            String[] str = list.get(0);
            String url = WordDefined.BASE_IMGE_URL + str[0] + "/" + str[1];
            json.put("success", 1);//一定要为数字类型
            json.put("message", "上传成功！");
            json.put("url", url);
            log.info(url);
            return json;
        }
        json.put("success", 0);//一定要为数字类型
        json.put("message", "上传失败！");
        json.put("url", "");
        return json;
    }

    @Transactional
    public void viewsIncreOne(Long articleId,Long userId){
        articleRepository.increViewTimes(articleId);
        userRepository.increViewSum(userId);
    }
}
