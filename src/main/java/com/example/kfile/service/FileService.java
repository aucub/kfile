package com.example.kfile.service;

import com.example.kfile.domain.result.FileEntry;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * 文件服务接口
 */
public interface FileService {

    /***
     * 获取指定路径下的文件及文件夹
     *
     */
    List<FileEntry> fileList(String fileItemId) throws Exception;

    /**
     * 获取单个文件信息
     */
    FileEntry getFileEntry(String fileItemId) throws FileNotFoundException;

    /**
     * 创建新文件夹
     */
    FileEntry newFolder(String directory, String name);

    /**
     * 删除文件
     */
    String deleteFile(String fileItemId) throws FileNotFoundException;

    /**
     * 复制文件
     */
    FileEntry copyFile(String fileItemId, String targetDirectory) throws FileNotFoundException;

    /**
     * 移动文件
     */
    FileEntry moveFile(String fileItemId, String targetDirectory);

    /**
     * 重命名文件
     */
    FileEntry renameFile(String fileItemId, String newName) throws FileNotFoundException;

    /**
     * 获取文件所有者
     */
    String getOwner(String fileId);
}