package io.github.xky717.forumX.service;

import io.github.xky717.forumX.util.RedisKeyUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;


    public void like(int userId, int entityTpy, int entityId, int entityUserId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityTpy,entityId);
                String userLikeKey = RedisKeyUtils.getUserLikeKey(entityUserId);
                boolean isMember =  operations.opsForSet().isMember(entityLikeKey,userId);

                operations.multi();
                if (isMember){
                    operations.opsForSet().remove(entityLikeKey,userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    operations.opsForSet().add(entityLikeKey,userId);
                    operations.opsForValue().increment(userLikeKey);
                }

                return operations.exec();
            }
        });


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

    //查询某个用户获得赞的数量
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtils.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }
}
