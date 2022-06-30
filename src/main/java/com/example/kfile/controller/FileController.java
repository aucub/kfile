package com.example.kfile.controller;

import com.example.kfile.entity.FileDetail;
import com.example.kfile.entity.Result;
import com.example.kfile.entity.request.*;
import com.example.kfile.entity.result.FileEntry;
import com.example.kfile.service.IFileItemService;
import com.example.kfile.service.IFileService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * 文件操作相关接口
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    IFileItemService fileItemService;

    @Autowired
    IFileService fileService;

    //创建文件夹
    @PostMapping("/mkdir")
    public Result mkdir(@Valid @RequestBody NewFolderRequest newFolderRequest) {
        return Result.success("创建成功", fileItemService.newFolder(newFolderRequest.getDirectory(), newFolderRequest.getName()));
    }

    @Transactional
    @PostMapping("/delete")
    public Result deleteFile(@Valid @RequestBody DeleteFileRequest deleteFileRequest) throws FileNotFoundException {
        return Result.success(fileItemService.deleteFile(deleteFileRequest.getId()));
    }

    //重命名文件
    @PostMapping("/rename")
    public Result rename(@Valid @RequestBody RenameFileRequest renameFileRequest) throws FileNotFoundException {
        if (fileItemService.renameFile(renameFileRequest.getId(), renameFileRequest.getNewName())) {
            return Result.success("重命名成功");
        } else {
            return Result.error("重命名失败");
        }
    }

    //移动文件
    @PostMapping("/move")
    public Result moveFile(@Valid @RequestBody MoveFileRequest moveFileRequest) {
        if (fileItemService.moveFile(moveFileRequest.getId(), moveFileRequest.getTarget())) {
            return Result.success("移动成功");
        }
        return Result.error("移动失败");
    }

    //复制文件
    @Transactional
    @PostMapping("/copy")
    public Result copyFile(@Valid @RequestBody CopyFileRequest copyFileRequest) throws FileNotFoundException {
        if (fileItemService.copyFile(copyFileRequest.getId(), copyFileRequest.getTarget())) {
            return Result.success("移动成功");
        }
        return Result.error("移动失败");
    }


    //上传文件
    @PostMapping("/upload")
    public Result uploadFile(@Valid @RequestBody UploadFileRequest uploadFileRequest, @RequestPart("file") MultipartFile file) {
        FileDetail fileDetail = fileItemService.checkUpload(uploadFileRequest);
        if (fileDetail != null) {
            fileItemService.newFile(uploadFileRequest, fileDetail);
        } else fileService.uploadFile(uploadFileRequest, file);
        return Result.success("上传成功");
    }

    @PostMapping("/list")
    public Result list(@Valid @RequestBody FileListRequest fileListRequest) {
        List<FileEntry> fileEntries = fileItemService.list(fileListRequest);
        if(fileEntries!=null&&fileEntries.size()>0){
            return Result.success(fileEntries);
        }
        else return Result.error("未找到文件");
    }

    @PostMapping("/info")
    public Result fileItem(@Valid @RequestBody FileInfoRequest fileInfoRequest) {
        try {
            FileEntry fileEntry = fileItemService.getFileEntry(fileInfoRequest.getId());
            return Result.success(fileEntry);
        } catch (FileNotFoundException e) {
            return Result.error("未找到文件");
        }
    }
}