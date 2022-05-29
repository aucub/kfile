package com.example.kfile.controller;

import io.minio.*;
import io.minio.errors.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class StorageFileController {

    MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://127.0.0.1:9001")
                    .credentials("sDRDIb2VaWKBRIn817J3", "AJMnqvSgWXNlCGwFp5lmYS3TqE76tDQv9itW1BFG")
                    .build();

    @GetMapping("/")
    public String uploadPage() {
        return "uploader";
    }

    public String download(String filemd5, int expires, String customFilename) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("response-content-disposition", "attachment; filename=\"" + customFilename + "\"");
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket("file").object(filemd5).expiry(expires).extraQueryParams(reqParams).build());
    }

    public String delete(String filemd5) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket("file").object(filemd5).build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }
        return "删除成功";
    }

    @GetMapping("/download/filemd5")
    public ResponseEntity<InputStreamResource> downloadFile(String filemd5, String filename) {
        try {
            InputStream stream = minioClient.getObject(GetObjectArgs.builder().bucket("file").object(filemd5).build());
            InputStreamResource resource = new InputStreamResource(stream);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Disposition", "attachment;filename=" + filename);
            return ResponseEntity.ok()
                    .headers(httpHeaders)
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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