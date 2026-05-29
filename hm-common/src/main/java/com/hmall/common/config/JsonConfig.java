package com.hmall.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;

@Configuration
@ConditionalOnClass(ObjectMapper.class) // 只有在类路径中存在 ObjectMapper 类时才会生效
public class JsonConfig {
    @Bean  // 自定义 Jackson 序列化配置
    // 解决 Long 类型精度丢失的问题
    // 全局配置，将所有的 Long 类型转换为 String 类型，避免精度丢失
    // 注意：如果有需要，也可以在具体的字段上使用 @JsonSerialize(using = ToStringSerializer.class) 注解来指定序列化方式
    // 例如：@JsonSerialize(using = ToStringSerializer.class) private Long id; // 只对 id 字段生效
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> {
            // long -> string
            jacksonObjectMapperBuilder.serializerByType(Long.class, ToStringSerializer.instance);
            jacksonObjectMapperBuilder.serializerByType(BigInteger.class, ToStringSerializer.instance);
        };
    }
}