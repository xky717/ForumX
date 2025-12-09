package io.github.xky717.forumX;

import io.github.xky717.forumX.util.MailClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = ForumXApplication.class)


public class MailTests {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("alimu.xiaokeya@tu-dortmund.de","TEST","welcome")  ;
    }

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username","sunday");

        String content = templateEngine.process("mail/demo",context);
        System.out.println(content);

        mailClient.sendMail("alimu.xiaokeya@tu-dortmund.de","HTML",content)  ;
    }

}
