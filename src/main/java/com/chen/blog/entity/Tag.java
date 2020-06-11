package com.chen.blog.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Table(name="tag")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })//解决转换异常
public class Tag {

    public interface ArticleTagView{}

    public interface HotTagView{}

    @JsonView({ArticleTagView.class,HotTagView.class})
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增长策略
    @Id
    private Integer id;

    @JsonView({ArticleTagView.class,HotTagView.class})
    @NotBlank(message = "标签名不能为空")
    @Column(nullable = false,name = "tag_name")
    private String tagName;

    @Column(nullable = false,columnDefinition = "int default 0")
    private Integer num;


    @ManyToMany(targetEntity = Article.class,fetch = FetchType.LAZY)
    @JoinTable(name = "join_tag_article",
            joinColumns = {@JoinColumn(name = "tag_id",referencedColumnName = "id", foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))},
            inverseJoinColumns = {@JoinColumn(name = "article_id",referencedColumnName = "id" , foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))},
            uniqueConstraints = {@UniqueConstraint(name ="unique_join_tag_article", columnNames = {"tag_id","article_id"})
    })
    List<Article> articleList;

}
