package com.chen.blog.service;

import com.chen.blog.common.WordDefined;
import com.chen.blog.entity.Article;
import com.chen.blog.entity.Comment;
import com.chen.blog.entity.User;
import com.chen.blog.exception.BlogException;
import com.chen.blog.repository.ArticleRepository;
import com.chen.blog.repository.CommentRepository;
import com.chen.blog.repository.UserRepository;
import com.chen.blog.utils.OthersUtils;
import com.chen.blog.utils.SensitivewordFilter;
import com.chen.blog.utils.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SensitivewordFilter sensitivewordFilter;

    public Page<Comment> getList(Pageable pageable, Long articleId, Integer tid) {
        Article article = getArticle(articleId);
        //公开 || 私有 == 登录的用户
        if (article.getType() == WordDefined.ARTICLE_OPEN || article.getUser().getId().equals(SessionUtils.getUserId())) {
            Page<Comment> page = commentRepository.findByArticleIsAndTid(article, tid, pageable);
            combineReplyCount(page);
            return page;
        }
        throw new BlogException(WordDefined.NO_ACCESS);
    }

    //组装回复数量
    private void combineReplyCount(Page<Comment> page) {
        List<Comment> comments = page.getContent();
        if (comments == null || comments.size() == 0) {
            return;
        }
        comments.parallelStream()
                .forEach(c -> {
                    Integer id = c.getId();
                    //顶级评论被回复总数量
                    int count = commentRepository.countByTid(id);
                    c.setReplyCount(count);
                });
    }

    //存在则返回，不存在抛出异常
    private Article getArticle(Long articleId) {
        Optional<Article> optionalArticle = articleRepository.findById(articleId);
        //不存在
        optionalArticle.orElseThrow(() -> new BlogException(WordDefined.ARTICLE_NOT_FOUNT));
        return optionalArticle.get();
    }


    public List<Comment> getReply(Long articleId, Integer tid) {
        Article article = getArticle(articleId);
        //公开 || 私有==登录的用户
        if (article.getType() == WordDefined.ARTICLE_OPEN || article.getUser().getId().equals(SessionUtils.getUserId())) {
            //通过创建时间排序
            List<Comment> comments = commentRepository.findByArticleIsAndTid(article, tid, Sort.by(Sort.Direction.ASC, "createtime"));
            combineNickname(comments);
            return comments;
        }
        throw new BlogException(WordDefined.NO_ACCESS);
    }

    //组装被回复者的昵称
    private void combineNickname(List<Comment> comments) {
        Map<Integer, String> map = comments.parallelStream()
                .collect(Collectors.toMap(Comment::getId, c -> c.getUser().getNickname()));

        comments.parallelStream()
                .forEach(c -> {
                    Integer cId = c.getCid();
                    String nickname = map.get(cId);
                    if (nickname == null) {
                        Optional<Comment> optionalComment = commentRepository.findById(cId);//只查找一次
                        if (optionalComment.isPresent()) {
                            nickname = optionalComment.get().getUser().getNickname();
                        }
                        map.put(cId, nickname == null ? "" : nickname);//即使是空也存进去
                    }
                    c.setNickname(nickname);
                });

    }

    @Transactional
    public void addReply(Comment comment) {
        User user = SessionUtils.getUser();
        comment.setUser(user);
        comment.setCreatetime(OthersUtils.getCreateTime());
        String reply = comment.getReply();
        //html转码+敏感词过滤
        String s = sensitivewordFilter.replaceSensitiveWord(HtmlUtils.htmlEscape(reply), 2, "*");
        comment.setReply(s);
        commentRepository.save(comment);
        //用户、文章评论数+1
        commentIncreOne(comment.getArticle().getId(),user.getId());
    }

    @Transactional
    public void commentIncreOne(Long articleId,Long userId){
        articleRepository.increCommentTimes(articleId);
        userRepository.increCommentSum(userId);
    }


}
