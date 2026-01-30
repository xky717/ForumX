package io.github.xky717.forumX.controller.interceptor;

import io.github.xky717.forumX.entity.LoginTicket;
import io.github.xky717.forumX.entity.User;
import io.github.xky717.forumX.service.UserService;
import io.github.xky717.forumX.util.CookieUtil;
import io.github.xky717.forumX.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@Component

public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //从Cookie中获取 ticket
        String ticket = CookieUtil.getValue(request,"ticket");

        if (ticket != null){
            //查询ticket
            LoginTicket loginTicket = userService.findloginTicket(ticket);
            //查询登陆Ticket是否有效
            if (loginTicket != null && loginTicket.getStatus() ==0 && loginTicket.getExpired().after(new Date())){
                //根据login ticket查询user
                User user = userService.findUserById(loginTicket.getUserId());
                //在本次请求中持有user info
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user!= null && modelAndView != null){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
