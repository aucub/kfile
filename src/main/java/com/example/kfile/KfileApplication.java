package com.example.kfile;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class KfileApplication {

    public static void main(String[] args) {
        SpringApplication.run(KfileApplication.class, args);
        try {
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint("http://127.0.0.1:9001")
                            .credentials("sDRDIb2VaWKBRIn817J3", "AJMnqvSgWXNlCGwFp5lmYS3TqE76tDQv9itW1BFG")
                            .build();
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket("file").build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("file").build());
            }
        } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("Error occurred: ");
        }
    }

}
