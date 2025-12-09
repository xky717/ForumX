package io.github.xky717.forumX.controller;

import io.github.xky717.forumX.entity.User;
import io.github.xky717.forumX.service.UserService;
import io.github.xky717.forumX.util.ForumxConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController implements ForumxConstant {

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage(){ return"/site/register"; }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage(){ return"/site/login"; }

    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String,Object> map = userService.register(user);
        if(map == null || map.isEmpty()){
            model.addAttribute("msg",
                    "register successfully,an activation-code has been send to your email.");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));

            return "/site/register";
        }
    }
    //http://localhost:8080/forumx/activation/101(用户id)/code(激活码)
    @RequestMapping(path= "http://localhost:8080/forumx/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code){
       int result = userService.activation(userId,code);
        if(result == ACTIVATION_SUCCESS){
            model.addAttribute("msg", "activation successfully.");
            model.addAttribute("target","/login");


        }else if(result == ACTIVATION_REPEAT){
            model.addAttribute("msg", "sorry,this account has been already activated.");
            model.addAttribute("target","/index");

        }else{
            model.addAttribute("msg", "activation failed, activation code invalid.");
            model.addAttribute("target","/index");

        }
        return "/site/operate-result";
    }
}
