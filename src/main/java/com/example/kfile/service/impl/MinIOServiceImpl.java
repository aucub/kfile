package com.example.kfile.service.impl;

import cn.xuyanwu.spring.file.storage.FileStorageService;
import cn.xuyanwu.spring.file.storage.platform.MinIOFileStorage;
import com.example.kfile.service.StorageService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class MinIOServiceImpl implements StorageService {
    private MinioClient minioClient;

    private FileStorageService fileStorageService;

    @Autowired
    public void setFileStorageService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public void setMinioClient(String platform) {
        MinIOFileStorage minIOFileStorage = (MinIOFileStorage) fileStorageService.getFileStorage(platform);
        this.minioClient = minIOFileStorage.getClient();
    }

    @Override
    public String getDownloadUrl(String platform, String path, String name, int expires) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Objects.requireNonNull(platform, "参数不能为空");
        Objects.requireNonNull(path, "参数不能为空");
        Objects.requireNonNull(name, "参数不能为空");
        if (expires <= 0) {
            throw new IllegalArgumentException("expires参数不合法");
        }
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("response-content-disposition", String.format("attachment; filename=\"%s\"", name));
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket("file")
                .object(path)
                .expiry(expires)
                .extraQueryParams(reqParams)
                .build());
    }
}