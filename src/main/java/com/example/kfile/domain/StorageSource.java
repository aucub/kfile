package com.example.kfile.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * TODO 根据需求修改，暂不使用
 */
@Data
@AllArgsConstructor
@Document("StorageSource")
public class StorageSource {
    @Id
    private String platform;
    private Boolean enableStorage;
    private String accessKey;
    private String secretKey;
    private String endPoint;
    private String bucketName;
    private String domain;
    private String basePath;
}
