package com.chen.blog.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Table(name="sort")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })//解决转换异常
public class Sort {

    public interface ArticleSortView{}

    public interface SortInfoView{}

    public interface AddSortView{}

    @JsonView({ArticleSortView.class,SortInfoView.class})
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增长策略
    @Id
    private Integer id;

    @JsonView({ArticleSortView.class,SortInfoView.class})
    @NotBlank(groups = {AddSortView.class},message = "分类名不能为空!")
    @Column(nullable = false,name = "sort_name")
    private String sortName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="blog_id",foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))//不生成外键
    private Blog blog;

    @OneToMany(targetEntity = Article.class,mappedBy = "sort")
    private List<Article> articleList;
}
