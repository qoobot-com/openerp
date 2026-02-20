package com.qoobot.qooerp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.qoobot.qooerp.**"})
public class AllStartApplication {
    public static void main(String[] args) {
        SpringApplication.run(AllStartApplication.class, args);
    }
}
