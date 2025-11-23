package io.github.xky717.forumX.service;


import io.github.xky717.forumX.dao.UserMapper;
import io.github.xky717.forumX.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }
}
