package io.github.xky717.forumX.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        //设置序列化方式 key
        template.setKeySerializer(RedisSerializer.string());
        //设置序列化方式 value
        template.setValueSerializer(RedisSerializer.json());
        //设置序列化方式 hash key
        template.setHashKeySerializer(RedisSerializer.string());
        //设置序列化方式 hash value
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();

        return template;
    }
}
