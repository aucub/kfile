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


    //检查文件是否冲突
    public static boolean checkConflict(List<FileItem> fileItems, FileItem fileItem) {
        //检查文件名是否与任何现有文件项冲突
        for (FileItem item : fileItems) {
            if (fileItem.getId().equals(item.getId())) {
                continue;
            }
            if (fileItem.getName().equals(item.getName())) {
                return false;
            }
        }
        return true;
    }
}
