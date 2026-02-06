package io.github.xky717.forumX.controller;

import io.github.xky717.forumX.entity.DiscussPost;
import io.github.xky717.forumX.entity.User;
import io.github.xky717.forumX.service.DiscussPostService;
import io.github.xky717.forumX.util.ForumxUtil;
import io.github.xky717.forumX.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController {

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    HostHolder hostHolder;

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

        //错误后面统一处理
        return ForumxUtil.getJSONString(0,"the post was successfully published.");
    }

}
