package com.example.kfile.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;

public interface IFileService {
    Boolean uploadFile(InputStream inputStream);

    Boolean delete(String fileInfoId);

    ResponseEntity<InputStreamResource> downloadFile(String fileItemId);
}
