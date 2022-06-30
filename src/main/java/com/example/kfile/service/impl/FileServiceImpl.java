package com.example.kfile.service.impl;

import com.example.kfile.entity.FileItem;
import com.example.kfile.service.FileDetailService;
import com.example.kfile.service.IFileItemService;
import com.example.kfile.service.IFileService;
import com.example.kfile.service.IUserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FileServiceImpl implements IFileService {

    IFileItemService fileItemService;

    FileDetailService fileDetailService;

    FileStorageService fileStorageService;

    IUserService userService;

    @Autowired
    public void setFileItemService(IFileItemService fileItemService) {
        this.fileItemService = fileItemService;
    }

    @Autowired
    public void setFileDetailService(FileDetailService fileDetailService) {
        this.fileDetailService = fileDetailService;
    }

    @Autowired
    public void setFileStorageService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadFile(String fileItemId) {
        try {
            FileItem fileItem = fileItemService.getById(fileItemId);
            FileInfo fileInfo = fileDetailService.getByUrl(fileItem.getUrl());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            fileStorageService.download(fileInfo).outputStream(byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
            InputStreamResource resource = new InputStreamResource(byteArrayInputStream);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Disposition", "attachment;filename=" + fileItem.getName());
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

    public Boolean delete(String fileInfoId) {
        fileStorageService.delete(fileDetailService.getByUrl(fileInfoId).getUrl());
        return true;
    }

    @Override
    public String uploadFile(InputStream inputStream) {
        try {
            String sha256Hash = DigestUtils.sha256Hex(inputStream);
            FileInfo fileInfo = fileStorageService.of(inputStream).putAttr("sha256sum", sha256Hash).image().upload();
            return sha256Hash;
        } catch (IOException e) {
            return "";
        }
    }

}
