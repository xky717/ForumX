package io.github.xky717.forumX.controller;

import io.github.xky717.forumX.dao.UserMapper;
import io.github.xky717.forumX.entity.User;
import io.github.xky717.forumX.service.UserService;
import io.github.xky717.forumX.util.ForumxUtil;
import io.github.xky717.forumX.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${forumx.path.upload}")
    private String uploadPath;

    @Value("${forumx.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;



    @RequestMapping(path = "/setting")
    public String getSettingPage(){
        return "/site/setting";
    }

    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) throws IOException {
        if (headerImage == null){
            model.addAttribute("error","please select a picture!");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix =  fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)){
            model.addAttribute("error","picture format incorrect");
            return "/site/setting";
        }

        //生成随机文件名
        fileName = ForumxUtil.generateUUID() + suffix;
        //存放路径
        File destination = new File(uploadPath +"/" + fileName);
        try {
            //存文件
            headerImage.transferTo(destination);
        } catch (IOException e) {
            logger.error("file update failed", e.getMessage());
            throw new RuntimeException("file upload failed"+e);
        }

        //更新头像路径（web path） http//.../user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName , HttpServletResponse response){
        //服务器存放路径
        fileName = uploadPath +"/"+fileName;
        //文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/" + suffix);

        try (
                FileInputStream inputStream = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
                ){

            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = inputStream.read(buffer)) != -1){
                os.write(buffer,0, b);
            }

        } catch (IOException e) {
            logger.error("read header picture failed" + e.getMessage());
            throw new RuntimeException(e);
        }

    }
    // 修改密码
  @RequestMapping(path="/updatePassword",method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, Model model
  ){
        User user = hostHolder.getUser();

       Map<String,Object> map = userService.updatePassword(user.getId(),oldPassword,newPassword);
       if(map.isEmpty() || map == null){
           return "redirect:/logout";
       }else{
           model.addAttribute("oldPasswordMsg",map.get("oldPasswordMsg"));
           model.addAttribute("newPasswordMsg",map.get("newpasswordMsg"));
           return "/site/setting";
       }
  }
}
