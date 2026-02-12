package io.github.xky717.forumX.controller;

import io.github.xky717.forumX.entity.Message;
import io.github.xky717.forumX.entity.Page;
import io.github.xky717.forumX.entity.User;
import io.github.xky717.forumX.service.MessageService;
import io.github.xky717.forumX.service.UserService;
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
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;
    @RequestMapping(value = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        //会话列表
        List<Message> conversationList = messageService.findConversations(
                user.getId(), page.getOffset(), page.getLimit());

        List<Map<String,Object>> conversations = new ArrayList<>();
        if (conversationList != null){
            for(Message message : conversationList){
                Map<String, Object> map = new HashMap<>();
                map.put("conversation",message);
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target",userService.findUserById(targetId));

                conversations.add(map);
            }
        }

        model.addAttribute("conversation",conversations);

        //查询未读
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);


        return "/site/letter";
    }



    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model){

        //分页
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        //私信列表
        List<Message> letterList =messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();
        if (letterList != null){
            for(Message message : letterList){
                Map<String,Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }

        model.addAttribute("letters",letters);
        //私信的目标 也就是跟当前用户私信的用户
        model.addAttribute("target",getLetterTarget(conversationId));
        //设置已读
        List<Integer> ids = getLetters(letterList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }


        return "site/letter-detail";
    }

    private List<Integer> getLetters(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        if (letterList != null){
            for(Message message : letterList){
                if (message.getToId() == hostHolder.getUser().getId() && message.getStatus() == 0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    private User getLetterTarget(String conversationId){
       String[] ids= conversationId.split("_");
       int id1 = Integer.parseInt(ids[0]);
       int id2 = Integer.parseInt(ids[1]);

       if (hostHolder.getUser().getId() == id1){
           return userService.findUserById(id2);
       }else {
           return userService.findUserById(id1);
       }

    }

    @RequestMapping(path="/letter/send" , method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content){
        User target = userService.findUserByName(toName);
        if (target == null){
            return ForumxUtil.getJSONString(1,"this user does not exist.");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }

        message.setContent(content);
        message.setCreateTime(new Date());

        messageService.addMessage(message);

        return ForumxUtil.getJSONString(0);

    }
}
