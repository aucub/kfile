package com.example.kfile.util;

import com.example.kfile.domain.FileDetail;
import com.example.kfile.domain.FileItem;
import com.example.kfile.domain.result.FileEntry;

public class FileUtil {
    public static FileEntry getFileEntry(FileItem fileItem, FileDetail fileDetail) {
        FileEntry fileEntry = (FileEntry) fileItem;
        fileEntry.setUrl(fileDetail.getUrl());
        fileEntry.setSize(fileDetail.getSize());
        fileEntry.setSha256sum(fileDetail.getSha256sum());
        return fileEntry;
    }
}
