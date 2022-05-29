package com.example.kfile.controller;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
public class FileController {

    @GetMapping("/")
    public String uploadPage() {
        return "index";
    }

    @PostMapping("/upload")
    @ResponseBody
    public String upload(@RequestPart("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }
        try {
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint("http://127.0.0.1:9001")
                            .credentials("sDRDIb2VaWKBRIn817J3", "AJMnqvSgWXNlCGwFp5lmYS3TqE76tDQv9itW1BFG")
                            .build();
            byte[] data = file.getBytes();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("file")                 // 存储桶名称
                            .object(file.getOriginalFilename())                 // 对象名称
                            .stream(
                                    new ByteArrayInputStream(data),  // 文件内容字节流
                                    data.length,                      // 文件大小
                                    -1                                // 选项，告诉 SDK 读取整个流
                            )
                            .build()
            );
            return "上传成功";
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            e.printStackTrace();
        }
        return "上传失败！";
    }

}
