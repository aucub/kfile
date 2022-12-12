package com.example.kfile.util;

import com.example.kfile.domain.FileDetail;
import com.example.kfile.domain.FileItem;
import com.example.kfile.domain.result.FileEntry;

import java.util.List;

public class FileUtil {
    public static FileEntry getFileEntry(FileItem fileItem, FileDetail fileDetail) {
        FileEntry fileEntry = (FileEntry) fileItem;
        fileEntry.setUrl(fileDetail.getUrl());
        fileEntry.setSize(fileDetail.getSize());
        fileEntry.setSha256sum(fileDetail.getSha256sum());
        return fileEntry;
    }

    //TODO
    public static Boolean check(List<FileItem> fileItems, FileItem fileItem) {
        return fileItems.contains(fileItem);
    }
}
