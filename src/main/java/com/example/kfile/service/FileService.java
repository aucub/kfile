package com.example.kfile.service;

import com.example.kfile.domain.FileDetail;
import com.example.kfile.domain.FileItem;
import com.example.kfile.domain.request.UploadFileRequest;
import com.example.kfile.domain.result.FileEntry;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * 文件服务接口
 */
public interface FileService {

    /**
     * 获取指定路径下的文件及文件夹
     *
     * @param fileItemId 文件项ID
     * @return 文件及文件夹列表
     * @throws Exception 异常
     */
    List<FileEntry> fileList(String fileItemId) throws Exception;

    /**
     * 获取单个文件信息
     *
     * @param fileItemId 文件项ID
     * @return 文件信息
     * @throws FileNotFoundException 文件未找到异常
     */
    FileEntry getFileEntry(String fileItemId) throws FileNotFoundException;

    /**
     * 创建新文件夹
     *
     * @param directory 目录
     * @param name      文件夹名称
     * @return 新文件夹的文件项
     */
    FileEntry newFolder(String directory, String name);

    FileItem newFile(UploadFileRequest uploadFileRequest, FileDetail fileDetail);

    FileDetail checkUpload(UploadFileRequest uploadFileRequest);

    /**
     * 删除文件
     *
     * @param fileItemId 文件项ID
     * @return 删除的文件项ID
     * @throws FileNotFoundException 文件未找到异常
     */
    String deleteFile(String fileItemId) throws FileNotFoundException;

    /**
     * 复制文件
     *
     * @param fileItemId      文件项ID
     * @param targetDirectory 目标目录
     * @return 复制后的文件项
     * @throws FileNotFoundException 文件未找到异常
     */
    FileEntry copyFile(String fileItemId, String targetDirectory) throws FileNotFoundException;

    /**
     * 移动文件
     *
     * @param fileItemId      文件项ID
     * @param targetDirectory 目标目录
     * @return 移动后的文件项
     */
    FileEntry moveFile(String fileItemId, String targetDirectory);

    /**
     * 重命名文件
     *
     * @param fileItemId 文件项ID
     * @param newName    新名称
     * @return 重命名后的文件项
     * @throws FileNotFoundException 文件未找到异常
     */
    FileEntry renameFile(String fileItemId, String newName) throws FileNotFoundException;

    /**
     * 获取文件所有者
     *
     * @param fileId 文件ID
     * @return 文件所有者
     */
    String getOwner(String fileId);
}