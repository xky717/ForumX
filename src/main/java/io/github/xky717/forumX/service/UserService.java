package io.github.xky717.forumX.service;


import io.github.xky717.forumX.dao.UserMapper;
import io.github.xky717.forumX.entity.User;
import io.github.xky717.forumX.util.ForumxConstant;
import io.github.xky717.forumX.util.ForumxUtil;
import io.github.xky717.forumX.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements ForumxConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${forumx.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();

        //空值处理
        if(user == null){
            throw new IllegalArgumentException("Parameters cannot be empty!");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","Username cannot be empty!");
            return map;
        }

        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","Password cannot be empty!");
            return map;
        }

        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","Email cannot be empty!");
            return map;
        }

        //验证账号
        User newUser = userMapper.selectByName(user.getUsername());
        if(newUser != null){
            map.put("usernameMsg","User already exist!");
            return map;
        }

        newUser = userMapper.selectByEmail(user.getEmail());
        if(newUser != null){
            map.put("emailMsg","Email already exist!");
            return map;
        }

        //注册用户
        user.setSalt(ForumxUtil.generateUUID().substring(0,5));
        user.setPassword(ForumxUtil.md5(user.getPassword()+ user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(ForumxUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        //激活路径-> http://localhost:8080/forumx/activation/101(用户id)/code(激活码)
        String url = domain + contextPath + "/activation/" + user.getId() + "/"+user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"Account activation",content);

        return map;
    }

    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILED;
        }

    }
}
