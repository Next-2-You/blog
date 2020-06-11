package com.chen.blog.config;

import com.chen.blog.vo.PageSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class BeanConfig {

    /**
     * 自定义序列化规则
     *
     * @return
     */
    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        return  new Jackson2ObjectMapperBuilder()
                .failOnUnknownProperties(false)
                .serializerByType(Page.class, new PageSerializer());
    }


}
