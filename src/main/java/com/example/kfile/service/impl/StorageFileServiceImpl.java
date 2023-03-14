package com.example.kfile.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.FileStorageService;
import com.example.kfile.domain.FileDetail;
import com.example.kfile.domain.FileItem;
import com.example.kfile.domain.request.UploadFileRequest;
import com.example.kfile.domain.result.FileEntry;
import com.example.kfile.repository.FileDetailRepository;
import com.example.kfile.repository.FileItemRepository;
import com.example.kfile.repository.StorageSourceRepository;
import com.example.kfile.service.FileService;
import com.example.kfile.service.StorageFileService;
import com.example.kfile.todo.StorageSourceFileOperatorException;
import com.example.kfile.util.CodeMsg;
import com.example.kfile.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

@Service
public class StorageFileServiceImpl implements StorageFileService {
    FileStorageService fileStorageService;
    FileService fileService;
    StorageSourceRepository storageSourceRepository;
    FileItemRepository fileItemRepository;
    FileDetailRepository fileDetailRepository;

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

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

    public ResponseEntity<InputStreamResource> downloadFile(String fileItemId) {
        try {
            FileItem fileItem = fileItemRepository.findById(fileItemId).get();
            FileInfo fileInfo = fileDetailRepository.findById(fileItem.getFileInfoId()).get();
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
        fileStorageService.delete(fileDetailRepository.findById(fileInfoId).get());
        return true;
    }

    public FileEntry uploadFile(UploadFileRequest uploadFileRequest, MultipartFile file) throws FileNotFoundException {
        FileInfo fileInfo = fileStorageService.of(file).setName(uploadFileRequest.getSha256sum()).upload();
        if (fileInfo == null) {
            throw new StorageSourceFileOperatorException(CodeMsg.STORAGE_SOURCE_FILE_PROXY_UPLOAD_FAIL, fileStorageService.getFileStorage().getPlatform(), "文件上传失败", null);
        }
        Optional<FileDetail> fileDetailOptional = fileDetailRepository.findById(fileInfo.getId());
        if (fileDetailOptional.isEmpty()) {
            throw new FileNotFoundException("上传出错,数据异常：" + fileInfo);
        }
        FileDetail fileDetail = fileDetailOptional.get();
        fileDetail.setSha256sum(uploadFileRequest.getSha256sum());
        fileDetail.setCreatedBy(StpUtil.getLoginId().toString());
        fileDetailRepository.save(fileDetail);
        if (uploadFileRequest.getFileItemId() != null) {
            return FileUtil.getFileEntry(fileService.newFile(uploadFileRequest, fileDetail), fileDetail);
        }
        return null;
    }
}
