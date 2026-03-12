package io.github.xky717.forumX;

import io.github.xky717.forumX.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.sql.SQLOutput;

@SpringBootTest
@ContextConfiguration(classes = ForumXApplication.class)
public class SensitiveTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void TestSensitiveFilter(){
        String text1 =" hallo bitch , fuck you, you are an asshole,huhu";
        String text = "⛤ hallo ⛤b⛤itch , ⛤fuc⛤k you, you are an ⛤as⛤shole,huhu";
        text = sensitiveFilter.filter(text);
        text1 = sensitiveFilter.filter(text1);
        System.out.println(text1);
        System.out.println(text);
    }
}
