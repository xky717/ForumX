package io.github.xky717.forumX.controller.advice;

import io.github.xky717.forumX.util.ForumxUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);
    @ExceptionHandler
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {

        logger.error("service error: {}", e.getMessage(), e);
        for(StackTraceElement element : e.getStackTrace()){
            logger.error(element.toString());
        }
        String xRequestedWith = request.getHeader("x-requested-with");
        //判断异步请求
        if("XMLHttpRequest".equals(xRequestedWith)){
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(ForumxUtil.getJSONString(1,"service error"));
        }else {
            response.sendRedirect(request.getContextPath()+"/error");
        }


    }
}
