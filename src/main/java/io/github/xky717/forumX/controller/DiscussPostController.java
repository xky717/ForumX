package io.github.xky717.forumX.controller;

import io.github.xky717.forumX.dao.CommentMapper;
import io.github.xky717.forumX.entity.*;
import io.github.xky717.forumX.event.EventProducer;
import io.github.xky717.forumX.service.CommentService;
import io.github.xky717.forumX.service.DiscussPostService;
import io.github.xky717.forumX.service.LikeService;
import io.github.xky717.forumX.service.UserService;
import io.github.xky717.forumX.util.ForumxConstant;
import io.github.xky717.forumX.util.ForumxUtil;
import io.github.xky717.forumX.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController implements ForumxConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content){

        User user =hostHolder.getUser();
        if (user == null){
            return ForumxUtil.getJSONString(403,"please login first.");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        //触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);


        //错误后面统一处理
        return ForumxUtil.getJSONString(0,"the post was successfully published.");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        //帖子
       DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
       model.addAttribute("post",post);
       //作者
       User user = userService.findUserById(post.getUserId());
       model.addAttribute("user",user);
       //帖子的点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeCount",likeCount);
        //帖子的点赞状态
        int likeStatus = hostHolder.getUser() ==null ? 0:
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeStatus",likeStatus);
       //评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        //评论：给帖子的评论
        //回复：给帖子的回复
        //评论列表
        List<Comment> commentList = commentService.findCommentByEntity(
                ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        //评论VO列表
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if (commentList != null){
            for (Comment comment : commentList){
                //评论VO
                Map<String,Object> commentVo = new HashMap<>();
                //评论
                commentVo.put("comment",comment);
                //作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                //评论的点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeCount",likeCount);
                //评论的点赞状态
                likeStatus = hostHolder.getUser() ==null ? 0:
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeStatus",likeStatus);

                //回复列表
                List<Comment> replyList = commentService.findCommentByEntity(
                        ENTITY_TYPE_COMMENT,comment.getId(), 0, Integer.MAX_VALUE);
                //回复VO列表
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if (replyList != null){
                    for (Comment reply :replyList){
                        Map<String,Object> replyVo = new HashMap<>();
                        //回复内容和作者
                        replyVo.put("reply",reply);
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        //回复目标用户:target
                        User target =  reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        //回复的点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeCount",likeCount);
                        //回复的点赞状态
                        likeStatus = hostHolder.getUser() ==null ? 0:
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeStatus",likeStatus);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replies", replyVoList);
                //回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("replyCount",replyCount);

                commentVoList.add(commentVo);


            }
        }

        model.addAttribute("comments",commentVoList);
       return "/site/discuss-detail";


    }

}
