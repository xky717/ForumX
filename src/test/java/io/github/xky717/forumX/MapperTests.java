package io.github.xky717.forumX;


import io.github.xky717.forumX.dao.DiscussPostMapper;
import io.github.xky717.forumX.dao.LoginTicketMapper;
import io.github.xky717.forumX.dao.MessageMapper;
import io.github.xky717.forumX.dao.UserMapper;
import io.github.xky717.forumX.entity.DiscussPost;
import io.github.xky717.forumX.entity.LoginTicket;
import io.github.xky717.forumX.entity.Message;
import io.github.xky717.forumX.entity.User;
import io.github.xky717.forumX.util.ForumxUtil;
import jakarta.xml.bind.SchemaOutputResolver;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = ForumXApplication.class)


public class MapperTests {

    private static final Logger log = LoggerFactory.getLogger(MapperTests.class);
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelecetUser(){
       User user =  userMapper.selectById(101);
       System.out.println(user);

       user = userMapper.selectByName("aaa");
       System.out.println(user);

       user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123");
        user.setEmail("aa@aa.com");
        user.setSalt("abc");
        user.setHeaderUrl("http://nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int row = userMapper.insertUser(user);
        System.out.println(row);
        System.out.println(user.getId());
    }
    @Test
    public void testInsertUser1() {
        User user = new User();
        user.setUsername("test2");                 // 新用户名，避免冲突
        user.setEmail("test2@test.com");

        // 1. 生成 salt
        String salt = ForumxUtil.generateUUID().substring(0, 5);
        user.setSalt(salt);

        // 2. 按系统规则加密密码
        String rawPassword = "123456";
        user.setPassword(ForumxUtil.md5(rawPassword + salt));

        // 3. 必须设置这些字段
        user.setType(0);
        user.setStatus(1);                         // 必须激活，否则登不上
        user.setActivationCode(null);              // 激活完成后可置 null
        user.setHeaderUrl("http://nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println("rows=" + rows);
        System.out.println("id=" + user.getId());
    }


    @Test
    public void testUpdateUser(){
       int rows = userMapper.updateStatus(150,1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150,"http://nowcoder.com/102.png");
        System.out.println(rows);

        rows= userMapper.updatePassword(150,"1111");
        System.out.println(rows);
    }

    @Test
    public void testSelectPosts(){
    List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149,0,10);
    for(DiscussPost post: list) {
        System.out.println(post);
    }

    int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }


    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+ 1000 * 60 *10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket(){
       LoginTicket loginTicket= loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc",0);
        loginTicket= loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }

    @Test
    public void testUpdateUserStatus(){
        User user = userMapper.selectById(154);
        System.out.println("userStatus"+ user.getStatus());
        userMapper.updateStatus(154,1);

        System.out.println("userName"+ user.getUsername());
        System.out.println("userStatus"+ user.getStatus());


    }

    @Test
    public void testSelectMapper(){
        List<Message> list = messageMapper.selectConversations(111,0,20);
        for (Message message : list){
            System.out.println(message);
        }
       int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        list = messageMapper.selectLetters("111_112",0,10);
        for (Message message : list){
            System.out.println(message);
        }
        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);

        count = messageMapper.selectLetterUnreadCount(131,"111_131");
        System.out.println(count);
    }

}

