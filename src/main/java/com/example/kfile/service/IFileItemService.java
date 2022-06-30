package com.example.kfile.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.kfile.entity.FileDetail;
import com.example.kfile.entity.FileItem;
import com.example.kfile.entity.request.FileListRequest;
import com.example.kfile.entity.request.UploadFileRequest;
import com.example.kfile.entity.result.FileEntry;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author aucub
 * @since 2023-11-12
 */
public interface IFileItemService extends IService<FileItem> {
    List<FileEntry> list(FileListRequest fileListRequest);

    FileEntry getFileEntry(String fileItemId) throws FileNotFoundException;

    Boolean newFolder(String directory, String name);

    Boolean newFile(UploadFileRequest uploadFileRequest, FileDetail fileDetail);

    FileDetail checkUpload(String sha256sum);

    Boolean deleteFile(String fileItemId);

    Boolean copyFile(String fileItemId, String targetDirectory);

    Boolean moveFile(String fileItemId, String targetDirectory);

    Boolean renameFile(String fileItemId, String newName);
}
