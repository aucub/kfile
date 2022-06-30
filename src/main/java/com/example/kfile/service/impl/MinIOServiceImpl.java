package com.example.kfile.service.impl;

import com.example.kfile.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class MinIOServiceImpl implements StorageService {


    @Override
    public String getDownloadUrl(String path) {
        return null;
    }

    @Override
    public String getDownloadUrl(String path, long expires) {
        return null;
    }

    @Override
    public InputStreamResource downloadFile(String path, String filename) {
        return null;
    }

    @Override
    public String delete(String path) {
        return null;
    }

    @Override
    public String upload(MultipartFile file) {
        return null;
    }
}