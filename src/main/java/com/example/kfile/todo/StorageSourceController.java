package com.example.kfile.todo;

import com.example.kfile.util.AjaxJson;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//TODO 根据需求修改
@RestController
public class StorageSourceController {
    private StorageSourceService storageSourceService;

    @Autowired
    public void setStorageSourceService(StorageSourceService storageSourceService) {
        this.storageSourceService = storageSourceService;
    }

    @ApiOperation(value = "获取存储源列表")
    @GetMapping("/list")
    public AjaxJson<List<StorageSource>> storageList() {
        return AjaxJson.getSuccessData(storageSourceService.findAll());
    }
}
