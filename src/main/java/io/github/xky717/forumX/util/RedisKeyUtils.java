package io.github.xky717.forumX.util;

public class RedisKeyUtils {

    private static final String SPLIT =":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    //某个实体的赞
    //like:entity:entityTyp:entityId -> set(userId)
    public static String getEntityLikeKey(int entityTyp, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityTyp + SPLIT + entityId;
    }

}
