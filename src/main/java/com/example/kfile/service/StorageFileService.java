package com.example.kfile.service;

import com.example.kfile.domain.request.UploadFileRequest;
import com.example.kfile.domain.result.FileEntry;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

public interface StorageFileService {
    ResponseEntity<InputStreamResource> downloadFile(String fileItemId);

    Boolean delete(String fileInfoId);

    FileEntry uploadFile(UploadFileRequest uploadFileRequest, MultipartFile file) throws FileNotFoundException;
}
