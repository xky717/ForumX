package io.github.xky717.forumX;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ForumXApplication {

    @PostConstruct
    public void init (){
        //解决netty启动冲突
        //see Netty4utils.setAvailableProcessors ()
        System.setProperty("es.set.netty.runtime.available.processors","false");
    }


	public static void main(String[] args) {
		SpringApplication.run(ForumXApplication.class, args);
	}

}
