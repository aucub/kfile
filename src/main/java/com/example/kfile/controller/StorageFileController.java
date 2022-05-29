package com.example.kfile.controller;

import io.minio.errors.*;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

//TODO  根据需求更改
@Controller
public class StorageFileController {

    public String getDownloadUrl(String filemd5, int expires, String customFilename) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return null;
    }

}