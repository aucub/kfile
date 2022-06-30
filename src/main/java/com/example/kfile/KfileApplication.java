package com.example.kfile;

import me.desair.tus.server.TusFileUploadService;
import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableFileStorage
@EnableScheduling
public class KfileApplication {

    @Value("${tus.upload.directory}")
    private String tusUploadDirectory;

    public static void main(String[] args) {
        SpringApplication.run(KfileApplication.class, args);
    }

    @Bean
    public TusFileUploadService tusFileUploadService() {
        return new TusFileUploadService().withStoragePath(tusUploadDirectory)
                .withUploadURI("/file/upload");
    }
}
