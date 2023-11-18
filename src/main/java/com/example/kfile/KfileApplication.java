package com.example.kfile;

import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableFileStorage
public class KfileApplication {

    public static void main(String[] args) {
        SpringApplication.run(KfileApplication.class, args);
    }
}
