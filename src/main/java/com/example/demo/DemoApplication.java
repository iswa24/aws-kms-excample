package com.example.demo;

import com.amazonaws.services.s3.AmazonS3Encryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

//    @Autowired
//    private AmazonS3Encryption encryptionClient;

    @PostConstruct
    public void test() {
//        encryptionClient.putObject()
    }
}
