package io.github.xky717.forumX.service;

import io.github.xky717.forumX.dao.CommentMapper;
import io.github.xky717.forumX.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    CommentMapper commentMapper;

    public List<Comment> findCommentByEntity(int entityTyp, int entityId, int offset, int limit){
        return commentMapper.selectCommentByEntity(entityTyp, entityId, offset, limit);
    }

    public int findCommentCount(int entityTyp, int entityId){
        return commentMapper.selectCountByEntity(entityTyp, entityId);
    }
}
