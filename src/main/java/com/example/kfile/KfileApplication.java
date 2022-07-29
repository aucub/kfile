package com.example.kfile;

import cn.xuyanwu.spring.file.storage.EnableFileStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@EnableFileStorage
@SpringBootApplication
public class KfileApplication {

    public static void main(String[] args) {
        SpringApplication.run(KfileApplication.class, args);
    }
}
