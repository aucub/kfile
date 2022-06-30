package com.example.kfile.service.impl;

import com.example.kfile.service.StorageService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class MinIOServiceImpl implements StorageService {

    MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://127.0.0.1:9001")
                    .credentials("sDRDIb2VaWKBRIn817J3", "AJMnqvSgWXNlCGwFp5lmYS3TqE76tDQv9itW1BFG")
                    .build();

    @Override
    public String getDownloadUrl(String path, String name, int expires) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("response-content-disposition", "attachment; filename=\"" + name + "\"");
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket("file").object(path).expiry(expires).extraQueryParams(reqParams).build());
    }

    public String delete(String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket("file").object(path).build());
        return "删除成功";
    }
}