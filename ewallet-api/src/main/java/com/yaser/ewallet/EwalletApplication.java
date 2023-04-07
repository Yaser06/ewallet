package com.yaser.ewallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EwalletApplication {

    public static void main(String[] args) {
        System.setProperty("jasypt.encryptor.password","my-secret-value");
        SpringApplication.run(EwalletApplication.class, args);
    }

}
