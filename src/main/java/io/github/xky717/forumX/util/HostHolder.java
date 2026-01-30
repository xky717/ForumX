package io.github.xky717.forumX.util;

import io.github.xky717.forumX.entity.User;
import org.springframework.stereotype.Component;

/*
   for holding user info instead of session object.
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
       users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
