package io.github.xky717.forumX.service;

import io.github.xky717.forumX.dao.CommentMapper;
import io.github.xky717.forumX.dao.DiscussPostMapper;
import io.github.xky717.forumX.entity.Comment;
import io.github.xky717.forumX.util.ForumxConstant;
import io.github.xky717.forumX.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements ForumxConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<Comment> findCommentByEntity(int entityTyp, int entityId, int offset, int limit){
        return commentMapper.selectCommentByEntity(entityTyp, entityId, offset, limit);
    }

    public int findCommentCount(int entityTyp, int entityId){
        return commentMapper.selectCountByEntity(entityTyp, entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        if (comment == null){
            throw new IllegalArgumentException("comment cant be empty");
        }

        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);

        //更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST){
            int count = commentMapper.selectCountByEntity(comment.getEntityType(),comment.getId());
            discussPostMapper.updateCommentCount(comment.getId(),count);
        }
        return rows;
    }
}
