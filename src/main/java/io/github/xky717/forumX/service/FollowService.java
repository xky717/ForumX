package io.github.xky717.forumX.service;

import io.github.xky717.forumX.entity.User;
import io.github.xky717.forumX.util.ForumxConstant;
import io.github.xky717.forumX.util.RedisKeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService implements ForumxConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    public void follow(int userId, int entityType, int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtils.getFolloweeKey(entityType, userId);
                String followerKey = RedisKeyUtils.getFollowerKey(entityType, entityId);

                operations.multi();

                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());

                return operations.exec();
            }
        });

    }

    public void unfollow(int userId, int entityType, int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtils.getFolloweeKey(entityType, userId);
                String followerKey = RedisKeyUtils.getFollowerKey(entityType, entityId);

                operations.multi();

                operations.opsForZSet().remove(followeeKey, entityId);
                operations.opsForZSet().remove(followerKey, userId);

                return operations.exec();
            }
        });

    }

    //查询关注的实体数量
    public long findFolloweeCount(int userId, int entityType){
        String followeeKey = RedisKeyUtils.getFolloweeKey(entityType, userId);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    //查询某个实体的粉丝数量
    public long findFollowerCount(int entityType, int entityId){
        String followerKey = RedisKeyUtils.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    //查询当前用户是否关注该实体
    public boolean hasFollowed(int userId, int entityType, int entityId){
        String followeeKey = RedisKeyUtils.getFolloweeKey(entityType, userId);
        return redisTemplate.opsForZSet().score(followeeKey,entityId) != null;
    }

    //查询某个用户关注的人
    public List<Map<String,Object>> findFollowees(int userId, int offset, int limit){
        String followeeKey = RedisKeyUtils.getFolloweeKey(ENTITY_TYPE_USER, userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset+limit -1);

        if (targetIds == null){
            return null;
        }
        List<Map<String,Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds){
            Map<String,Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user",user);
            Double score = redisTemplate.opsForZSet().score(followeeKey,targetId);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    //查询某个用户的粉丝
    public List<Map<String,Object>> findFollowers(int userId, int offset, int limit){
        String followerKey = RedisKeyUtils.getFollowerKey(ENTITY_TYPE_USER,userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset+limit -1);

        if (targetIds == null){
            return null;
        }
        List<Map<String,Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds){
            Map<String,Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user",user);
            Double score = redisTemplate.opsForZSet().score(followerKey,targetId);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

}
