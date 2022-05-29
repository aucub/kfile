package com.example.kfile.service;

import com.example.kfile.service.impl.FileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 检查请求
 */
@Service
public class FileAccessCheckService {

    FileServiceImpl fileServiceImpl;

    @Autowired
    public void setFileUtil(FileServiceImpl fileServiceImpl) {
        this.fileServiceImpl = fileServiceImpl;
    }

    public int checkFileIsInaccessible(String fileId, String userName) {
        if (fileServiceImpl.getOwner(fileId).equals(userName)) {
            return 7;
        }
        return 0;
    }
}