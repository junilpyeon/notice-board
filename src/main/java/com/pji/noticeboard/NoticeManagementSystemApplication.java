package com.pji.noticeboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class NoticeManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoticeManagementSystemApplication.class, args);
    }

}
