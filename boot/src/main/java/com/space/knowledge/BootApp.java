package com.space.knowledge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BootApp {
    
    @GetMapping("/")
    public String home() {
        return "Hello, World! 航天知识闯关学习平台";
    }
    
    public static void main(String[] args) {
        SpringApplication.run(BootApp.class, args);
    }
}
