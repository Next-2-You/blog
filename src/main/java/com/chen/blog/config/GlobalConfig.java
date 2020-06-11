package com.chen.blog.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/logoreg").setViewName("direct/logoreg");
        registry.addViewController("/home/{userId}").setViewName("direct/home");
        registry.addViewController("/sort/{sortId}").setViewName("direct/sort");
        registry.addViewController("/edit/{articleId}").setViewName("security/edit");
        registry.addViewController("/edit").setViewName("security/edit");
        registry.addViewController("/manage").setViewName("security/manage");
        registry.addViewController("/search/author").setViewName("direct/author-search");
        registry.addViewController("/search/article").setViewName("direct/article-search");
        registry.addViewController("/details/{userId}/{articleId}").setViewName("direct/details");
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/center").setViewName("security/center");
    }
}
