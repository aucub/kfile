package com.example.kfile.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.xuyanwu.spring.file.storage.FileInfo;
import com.example.kfile.domain.FileDetail;
import com.example.kfile.domain.request.*;
import com.example.kfile.domain.result.FileEntry;
import com.example.kfile.exception.StorageSourceFileOperatorException;
import com.example.kfile.service.FileService;
import com.example.kfile.util.AjaxJson;
import com.example.kfile.util.CodeMsg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.Optional;

/**
 * 文件操作相关接口, 如新建文件夹, 上传文件, 删除文件, 移动文件等.
 */
@Api(tags = "文件操作模块")
@Slf4j
@RestController
@RequestMapping("/api/file/operator")
public class FileOperatorController {

    FileService fileService;

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @ApiOperation(value = "创建文件夹")
    @PostMapping("/mkdir")
    public AjaxJson<?> mkdir(@Valid @RequestBody NewFolderRequest newFolderRequest) {
        return AjaxJson.getSuccess("创建成功", fileService.newFolder(newFolderRequest.getDirectory(), newFolderRequest.getName()));
    }

    @ApiOperation(value = "删除文件/文件夹")
    @Transactional
    @PostMapping("/delete/batch")
    public AjaxJson<?> deleteFile(@Valid @RequestBody String fileItemId) throws FileNotFoundException {
        return AjaxJson.getSuccess(fileService.deleteFile(fileItemId));
    }


    @ApiOperation(value = "重命名文件")
    @PostMapping("/rename/file")
    public AjaxJson<?> rename(@Valid @RequestBody RenameFileRequest renameFileRequest) throws FileNotFoundException {
        FileEntry fileEntry = fileService.renameFile(renameFileRequest.getFileId(), renameFileRequest.getNewName());
        if (fileEntry.getName().equals(renameFileRequest.getNewName())) {
            return AjaxJson.getSuccess("重命名成功", fileEntry);
        } else {
            return AjaxJson.getError("重命名失败");
        }
    }

    @ApiOperation(value = "上传文件")
    @PostMapping("/upload/file")
    // TODO 上传文件
    public AjaxJson<?> uploadFile(@Valid @RequestBody UploadFileRequest uploadFileRequest, @RequestPart("file") MultipartFile file) throws FileNotFoundException {
        FileInfo fileInfo = fileStorageService.of(file).setName(uploadFileRequest.getSha256sum()).upload();
        if (fileInfo == null) {
            throw new StorageSourceFileOperatorException(CodeMsg.STORAGE_SOURCE_FILE_PROXY_UPLOAD_FAIL, fileStorageService.getFileStorage().getPlatform(), "文件上传失败", null);
        }
        Optional<FileDetail> fileDetailOptional = fileDetailRepository.findById(fileInfo.getId());
        if (fileDetailOptional.isEmpty()) {
            throw new FileNotFoundException("上传出错,数据异常：" + fileInfo);
        }
        FileDetail fileDetail = fileDetailOptional.get();
        fileDetail.setSha256sum(uploadFileRequest.getSha256sum());
        fileDetail.setCreatedBy(StpUtil.getLoginId().toString());
        fileDetailRepository.save(fileDetail);
        if (uploadFileRequest.getFileItemId() != null) {

        }
        return AjaxJson.getSuccess("上传成功");
    }


    @ApiOperation(value = "移动文件")
    @PostMapping("/move/file")
    public AjaxJson<?> moveFile(@Valid @RequestBody MoveFileRequest moveFileRequest) {
        FileEntry fileEntry = fileService.moveFile(moveFileRequest.getFileItemId(), moveFileRequest.getTarget());
        if (fileEntry.getDirectory().equals(moveFileRequest.getTarget())) {
            return AjaxJson.getSuccess("移动成功");
        }
        return AjaxJson.getError("移动失败");
    }

    @ApiOperation(value = "复制文件")
    @Transactional
    @PostMapping("/copy/file")
    public AjaxJson<?> copyFile(@Valid @RequestBody CopyFileRequest copyFileRequest) throws FileNotFoundException {
        FileEntry fileEntry = fileService.copyFile(copyFileRequest.getFileItemId(), copyFileRequest.getTarget());
        if (fileEntry.getDirectory().equals(copyFileRequest.getTarget())) {
            return AjaxJson.getSuccess("复制成功" + fileEntry);
        }
        return AjaxJson.getError("复制失败");
    }
}