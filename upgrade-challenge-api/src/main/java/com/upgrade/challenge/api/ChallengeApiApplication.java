package com.upgrade.challenge.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.upgrade.challenge"})
public class ChallengeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChallengeApiApplication.class, args);
    }
}
