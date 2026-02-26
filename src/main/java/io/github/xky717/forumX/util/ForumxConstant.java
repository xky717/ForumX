package io.github.xky717.forumX.util;

public interface ForumxConstant {
    /**
    激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
    重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
    激活失败
     */
    int ACTIVATION_FAILED = 2;

    /**
    常规cookie保存时间
     */
    int DEFAULT_EXPIRED_SECOND = 3600 * 12;

    /**
    记住我，cookie保存时间
     */
    int REMEMBERME_EXPIRED_SECOND = 3600 * 24 * 100;

    /**
     * 实体类型：帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型：评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型：评论
     */
    int ENTITY_TYPE_USER = 3;
}

