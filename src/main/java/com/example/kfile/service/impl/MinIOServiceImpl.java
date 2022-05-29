package com.example.kfile.service.impl;

import com.example.kfile.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class MinIOServiceImpl implements StorageService {

    @Override
    public String getDownloadUrl(String path, long expires) {
        return null;
    }
}