package com.example.kfile.service;

import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.FileStorageService;
import cn.xuyanwu.spring.file.storage.platform.FileStorage;
import cn.xuyanwu.spring.file.storage.platform.MinIOFileStorage;
import com.example.kfile.domain.FileItem;
import com.example.kfile.domain.StorageSource;
import com.example.kfile.repository.FileDetailRepository;
import com.example.kfile.repository.FileItemRepository;
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

/**
 * TODO  根据需求更改
 */
@Service
public class BaseStorageService {
    FileStorageService fileStorageService;
    StorageSourceRepository storageSourceRepository;

    FileItemRepository fileItemRepository;

    FileDetailRepository fileDetailRepository;

    @Autowired
    public void setFileItemRepository(FileItemRepository fileItemRepository) {
        this.fileItemRepository = fileItemRepository;
    }

    @Autowired
    public void setFileDetailRepository(FileDetailRepository fileDetailRepository) {
        this.fileDetailRepository = fileDetailRepository;
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

    public ResponseEntity<InputStreamResource> downloadFile(String fileItemId, String filename) {
        try {
            FileItem fileItem = fileItemRepository.findById(fileItemId).get();
            FileInfo fileInfo = fileDetailRepository.findById(fileItem.getFileInfoId()).get();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            fileStorageService.download(fileInfo).outputStream(byteArrayOutputStream);
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

    public Boolean delete(String fileItemId) {
        fileStorageService.delete(fileDetailRepository.findById(fileItemRepository.findById(fileItemId).get().getFileInfoId()).get());
        return true;
    }

    public String upload(MultipartFile file) {
        return fileStorageService.of(file).upload().getUrl();
    }
}
