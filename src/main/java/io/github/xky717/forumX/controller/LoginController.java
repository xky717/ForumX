package io.github.xky717.forumX.controller;

import com.google.code.kaptcha.Producer;
import io.github.xky717.forumX.entity.User;
import io.github.xky717.forumX.service.UserService;
import io.github.xky717.forumX.util.ForumxConstant;
import jakarta.servlet.http.Cookie;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;


@Controller
public class LoginController implements ForumxConstant {

    private Logger logger;

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

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

    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session) throws IOException {
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //验证码存入session
        session.setAttribute("kaptcha",text);

        //将图片输出给浏览器
        response.setContentType("image/png");

        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应码验证失败:{}", e.getMessage());
        }
    }


    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberMe,
                        Model model, HttpSession session, HttpServletResponse response){

        //检查验证码
        String kaptcha = session.getAttribute("kaptcha").toString();
        if (StringUtils.isBlank(kaptcha)|| StringUtils.isBlank(code)|| !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","code invalid");
            return "/site/login";
        }

        //检查账号和密码
        int expiredSecond = rememberMe? REMEMBERME_EXPIRED_SECOND : DEFAULT_EXPIRED_SECOND;
        Map<String,Object> map= userService.login(username,password,expiredSecond);
        if (map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSecond);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path="/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";

    }


}