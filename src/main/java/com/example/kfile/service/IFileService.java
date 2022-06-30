package com.example.kfile.service;

import com.example.kfile.entity.FileItem;
import com.example.kfile.entity.request.UploadFileRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {
    FileItem uploadFile(UploadFileRequest uploadFileRequest, MultipartFile file);

    Boolean delete(String fileInfoId);

    ResponseEntity<InputStreamResource> downloadFile(String fileItemId);
}
