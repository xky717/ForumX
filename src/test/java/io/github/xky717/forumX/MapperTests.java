package io.github.xky717.forumX;


import io.github.xky717.forumX.dao.DiscussPostMapper;
import io.github.xky717.forumX.dao.UserMapper;
import io.github.xky717.forumX.entity.DiscussPost;
import io.github.xky717.forumX.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = ForumXApplication.class)


public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

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
    public void testUpdateUser(){
       int rows = userMapper.updateStatus(150,1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150,"http://nowcoder.com/102.png");
        System.out.println(rows);

        rows= userMapper.updatePaasword(150,"1111");
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


}

