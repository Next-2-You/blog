package com.chen.blog.vo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.data.domain.Page;

import java.io.IOException;

/**
 * 解决使用@JsonView时无法序列化Page问题
 *
 */
public class PageSerializer extends JsonSerializer<Page> {
    @Override
    public void serialize(Page page, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        ObjectMapper om = new ObjectMapper().disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("totalElements", page.getTotalElements());
        jsonGenerator.writeNumberField("totalPages",page.getTotalPages());
        jsonGenerator.writeNumberField("pageNo",page.getNumber());
        jsonGenerator.writeNumberField("pageSize",page.getSize());
        jsonGenerator.writeBooleanField("last", page.isLast());
        jsonGenerator.writeBooleanField("first", page.isFirst());
        jsonGenerator.writeFieldName("content");
        jsonGenerator.writeRawValue(
                om.writerWithView(serializerProvider.getActiveView()).writeValueAsString(page.getContent())
        );
        jsonGenerator.writeEndObject();
    }
}
