package io.github.xky717.forumX.service;

import io.github.xky717.forumX.util.RedisKeyUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;


    public void like(int userId, int entityTpy, int entityId){
        String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityTpy,entityId);
        boolean isMember =  redisTemplate.opsForSet().isMember(entityLikeKey,userId);
        if (isMember){
            redisTemplate.opsForSet().remove(entityLikeKey,userId);
        } else {
            redisTemplate.opsForSet().add(entityLikeKey,userId);
        }
    }

    //查询某实体的点赞量
    public long findEntityLikeCount(int entityTyp, int entityId){
        String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityTyp,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    //查询某用户对某实体的点赞状态
    public int findEntityLikeStatus(int userId, int entityTyp, int entityId){
        String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityTyp,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId)? 1 : 0 ;
    }
}
