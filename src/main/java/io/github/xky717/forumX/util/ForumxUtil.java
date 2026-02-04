package io.github.xky717.forumX.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class ForumxUtil {

    public static final int SUCCESS = 0;
    public static final int ERROR = 1;


    //生随机字符串(激活码) oder 密码加盐
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //MD5加密
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null);
    }

    public static String getJSONString(int code, String msg) {
        if (msg == null) {
            return "{\"code\":" + code + "}";
        }
        return "{\"code\":" + code + ",\"msg\":\"" + msg + "\"}";
    }

}

