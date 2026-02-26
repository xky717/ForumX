package io.github.xky717.forumX.controller;

import io.github.xky717.forumX.entity.User;
import io.github.xky717.forumX.service.FollowService;
import io.github.xky717.forumX.util.ForumxUtil;
import io.github.xky717.forumX.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FollowController {

    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path="/follow",method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId){
        User user = hostHolder.getUser();
        if (user == null){
            throw new RuntimeException("this user does not exist");
        }
        followService.follow(user.getId(),entityType,entityId);
        return ForumxUtil.getJSONString(0,"followed!");
    }

    @RequestMapping(path="/unfollow",method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        User user = hostHolder.getUser();
        if (user == null){
            throw new RuntimeException("this user does not exist");
        }
        followService.unfollow(user.getId(),entityType,entityId);
        return ForumxUtil.getJSONString(0,"unfollowed!");
    }
}
