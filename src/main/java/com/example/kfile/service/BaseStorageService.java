package com.example.kfile.service;

import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.FileStorageService;
import cn.xuyanwu.spring.file.storage.platform.FileStorage;
import cn.xuyanwu.spring.file.storage.platform.MinIOFileStorage;
import com.example.kfile.domain.StorageSource;
import com.example.kfile.repository.FileInfoRepository;
import com.example.kfile.repository.StorageSourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class BaseStorageService {
    FileStorageService fileStorageService;
    StorageSourceRepository storageSourceRepository;

    FileInfoRepository fileInfoRepository;

    @Autowired
    public void setFileInfoRepository(FileInfoRepository fileInfoRepository) {
        this.fileInfoRepository = fileInfoRepository;
    }

    @Autowired
    public void setStorageSourceRepository(StorageSourceRepository storageSourceRepository) {
        this.storageSourceRepository = storageSourceRepository;
    }

    @Autowired
    public void setFileStorageService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public Boolean initStorageSource() {
        //获得存储平台 List
        CopyOnWriteArrayList<FileStorage> list = fileStorageService.getFileStorageList();
        list.clear();
        for (StorageSource storageSource : storageSourceRepository.findAll()) {
            //增加
            MinIOFileStorage storage = new MinIOFileStorage();
            storage.setPlatform(storageSource.getPlatform());//平台名称
            storage.setBasePath(storageSource.getBasePath());
            storage.setDomain(storageSource.getDomain());
            storage.setAccessKey(storageSource.getAccessKey());
            storage.setSecretKey(storageSource.getSecretKey());
            storage.setBucketName(storageSource.getBucketName());
            storage.setEndPoint(storageSource.getEndPoint());
            list.add(storage);
        }
        return true;
    }

    public ResponseEntity<InputStreamResource> downloadFile(String fileId, String filename) {
        try {
            com.example.kfile.domain.FileInfo fileInfo = fileInfoRepository.findById(fileId).get();
            FileInfo fileInfo1 = fileStorageService.getFileInfoByUrl(fileInfo.getUrl());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            fileStorageService.download(fileInfo1).outputStream(byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
            InputStreamResource resource = new InputStreamResource(byteArrayInputStream);
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

    public Boolean delete(String fileId) {
        fileStorageService.delete(fileInfoRepository.findById(fileId).get().getUrl());
        return true;
    }

    public String upload(MultipartFile file) {
        return fileStorageService.of(file).upload().getUrl();
    }
}
