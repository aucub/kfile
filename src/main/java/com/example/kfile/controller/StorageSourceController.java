package com.example.kfile.controller;

import com.example.kfile.domain.StorageSource;
import com.example.kfile.repository.StorageSourceRepository;
import com.example.kfile.util.AjaxJson;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StorageSourceController {
    private StorageSourceRepository storageSourceRepository;

    @ApiOperation(value = "获取存储源列表")
    @GetMapping("/list")
    public AjaxJson<List<StorageSource>> storageList() {
        return AjaxJson.getSuccessData(storageSourceRepository.findByEnableStorageTrue());
    }
}
