package com.example.kfile.controller;

import com.example.kfile.entity.FileDetail;
import com.example.kfile.entity.Result;
import com.example.kfile.entity.request.*;
import com.example.kfile.service.IFileItemService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

/**
 * 文件操作相关接口, 如新建文件夹, 上传文件, 删除文件, 移动文件等.
 */
@Slf4j
@RestController
@RequestMapping("/api/file/operator")
public class FileOperatorController {

    IFileItemService fileItemService;

    public void setFileItemService(IFileItemService fileItemService) {
        this.fileItemService = fileItemService;
    }

    //创建文件夹")
    @PostMapping("/mkdir")
    public Result mkdir(@Valid @RequestBody NewFolderRequest newFolderRequest) {
        return Result.success("创建成功", fileItemService.newFolder(newFolderRequest.getDirectory(), newFolderRequest.getName()));
    }

    //删除文件/文件夹")
    @Transactional
    @PostMapping("/delete/batch")
    public Result deleteFile(@Valid @RequestBody String fileItemId) throws FileNotFoundException {
        return Result.success(fileItemService.deleteFile(fileItemId));
    }


    //重命名文件")
    @PostMapping("/rename/file")
    public Result rename(@Valid @RequestBody RenameFileRequest renameFileRequest) throws FileNotFoundException {
        FileItem fileItem = fileItemService.renameFile(renameFileRequest.getFileId(), renameFileRequest.getNewName());
        if (fileItem.getName().equals(renameFileRequest.getNewName())) {
            return Result.success("重命名成功", fileItem);
        } else {
            return Result.error("重命名失败");
        }
    }

    //上传文件")
    @PostMapping("/upload/file")
    public Result uploadFile(@Valid @RequestBody UploadFileRequest uploadFileRequest, @RequestPart("file") MultipartFile file) throws FileNotFoundException {
        FileDetail fileDetail = fileItemService.checkUpload(uploadFileRequest);
        if (fileDetail != null) {
            fileService.newFile(uploadFileRequest, fileDetail);
        } else storageFileService.uploadFile(uploadFileRequest, file);
        return Result.success("上传成功");
    }


    //移动文件")
    @PostMapping("/move/file")
    public Result moveFile(@Valid @RequestBody MoveFileRequest moveFileRequest) {
        FileEntry fileEntry = fileService.moveFile(moveFileRequest.getFileItemId(), moveFileRequest.getTarget());
        if (fileEntry.getDirectory().equals(moveFileRequest.getTarget())) {
            return Result.success("移动成功");
        }
        return AjaxJson.getError("移动失败");
    }

    //复制文件")
    @Transactional
    @PostMapping("/copy/file")
    public Result copyFile(@Valid @RequestBody CopyFileRequest copyFileRequest) throws FileNotFoundException {
        FileEntry fileEntry = fileService.copyFile(copyFileRequest.getFileItemId(), copyFileRequest.getTarget());
        if (fileEntry.getDirectory().equals(copyFileRequest.getTarget())) {
            return Result.success("复制成功" + fileEntry);
        }
        return AjaxJson.getError("复制失败");
    }
}