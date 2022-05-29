package com.example.kfile.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.example.kfile.domain.result.FileEntry;
import com.example.kfile.service.FileService;
import com.example.kfile.service.ShareLinkService;
import com.example.kfile.service.StorageService;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
public class StorageFileController {

    FileService fileService;

    StorageService storageService;

    ShareLinkService shareLinkService;

    @Autowired
    public void setShareLinkService(ShareLinkService shareLinkService) {
        this.shareLinkService = shareLinkService;
    }

    @Autowired
    public void setStorageService(StorageService storageService) {
        this.storageService = storageService;
    }

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    public String getDownloadUrl(String fileItemId, int expires) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (fileService.getOwner(fileItemId).equals(StpUtil.getLoginId().toString())) {
            FileEntry fileEntry = fileService.getFileEntry(fileItemId);
            return storageService.getDownloadUrl(fileEntry.getSha256sum(), fileEntry.getName(), expires);
        }
        return null;
    }

    public String getDownloadUrl(String fileItemId, String url) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        FileEntry fileEntry = fileService.getFileEntry(fileItemId);
        if (shareLinkService.getShareOfAcl(fileEntry.getShare(), fileItemId) > 0) {
            return storageService.getDownloadUrl(fileEntry.getSha256sum(), fileEntry.getName(), 60 * 60 * 24);
        }
        return null;
    }

}