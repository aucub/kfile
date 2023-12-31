package com.example.kfile.controller;

import com.example.kfile.entity.FileDetail;
import com.example.kfile.entity.FileItem;
import com.example.kfile.entity.Result;
import com.example.kfile.entity.enums.FilePermissionEnum;
import com.example.kfile.entity.enums.FileTypeEnum;
import com.example.kfile.entity.request.*;
import com.example.kfile.entity.result.FileEntry;
import com.example.kfile.security.FilePermissionCheck;
import com.example.kfile.service.IFileItemService;
import com.example.kfile.service.IFileService;
import com.example.kfile.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 文件操作相关接口
 */
@Slf4j
@RestController
@RequestMapping("/file")
@CrossOrigin(exposedHeaders = {"Location", "Upload-Offset"})
public class FileController {

    IFileItemService fileItemService;

    IFileService fileService;

    IUserService userService;

    private TusFileUploadService tusFileUploadService=new TusFileUploadService().withStoragePath("./build/tmp/upload/tus");

    @Autowired
    public void setFileItemService(IFileItemService fileItemService) {
        this.fileItemService = fileItemService;
    }
    @Autowired
    public void setFileService(IFileService fileService) {
        this.fileService = fileService;
    }
    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    //创建文件夹
    @PostMapping("/mkdir")
    @FilePermissionCheck({
            @FilePermissionCheck.FieldPermission(field = "directory", permission = FilePermissionEnum.UPLOAD)
    })
    public Result mkdir(@Valid @RequestBody NewFolderRequest newFolderRequest) {
        return Result.success("创建成功", fileItemService.newFolder(newFolderRequest.getDirectory(), newFolderRequest.getName()));
    }

    @PostMapping("/delete")
    @FilePermissionCheck({
            @FilePermissionCheck.FieldPermission(field = "id", permission = FilePermissionEnum.OWNER)
    })
    public Result deleteFile(@Valid @RequestBody DeleteFileRequest deleteFileRequest) {
        return Result.success(fileItemService.deleteFile(deleteFileRequest.getId()));
    }

    //重命名文件
    @PostMapping("/rename")
    @FilePermissionCheck({
            @FilePermissionCheck.FieldPermission(field = "id", permission = FilePermissionEnum.OWNER)
    })
    public Result rename(@Valid @RequestBody RenameFileRequest renameFileRequest) {
        if (fileItemService.renameFile(renameFileRequest.getId(), renameFileRequest.getNewName())) {
            return Result.success("重命名成功");
        } else {
            return Result.error("重命名失败");
        }
    }

    //移动文件
    @PostMapping("/move")
    @FilePermissionCheck({
            @FilePermissionCheck.FieldPermission(field = "id", permission = FilePermissionEnum.OWNER),
            @FilePermissionCheck.FieldPermission(field = "target", permission = FilePermissionEnum.OWNER)
    })
    public Result moveFile(@Valid @RequestBody MoveFileRequest moveFileRequest) {
        if (fileItemService.moveFile(moveFileRequest.getId(), moveFileRequest.getTarget())) {
            return Result.success("移动成功");
        }
        return Result.error("移动失败");
    }

    //复制文件
    @Transactional
    @PostMapping("/copy")
    @FilePermissionCheck({
            @FilePermissionCheck.FieldPermission(field = "id", permission = FilePermissionEnum.DOWNLOAD),
            @FilePermissionCheck.FieldPermission(field = "target", permission = FilePermissionEnum.OWNER)
    })
    public Result copyFile(@Valid @RequestBody CopyFileRequest copyFileRequest) {
        if (fileItemService.copyFile(copyFileRequest.getId(), copyFileRequest.getTarget())) {
            return Result.success("移动成功");
        }
        return Result.error("移动失败");
    }

    @PostMapping("/list")
    @FilePermissionCheck({
            @FilePermissionCheck.FieldPermission(field = "directory", permission = FilePermissionEnum.ACCESS)
    })
    public Result list(@Valid @RequestBody FileListRequest fileListRequest) {
        List<FileEntry> fileEntries = fileItemService.list(fileListRequest);
        if (fileEntries != null && !fileEntries.isEmpty()) {
            return Result.success(fileEntries);
        } else return Result.error("未找到文件");
    }

    @PostMapping("/info")
    @FilePermissionCheck({
            @FilePermissionCheck.FieldPermission(field = "id", permission = FilePermissionEnum.ACCESS)
    })
    public Result fileItem(@Valid @RequestBody FileInfoRequest fileInfoRequest) {
        try {
            FileEntry fileEntry = fileItemService.getFileEntry(fileInfoRequest.getId());
            return Result.success(fileEntry);
        } catch (FileNotFoundException e) {
            return Result.error("未找到文件");
        }
    }

    @RequestMapping(value = {"/upload", "/upload/**"}, method = {RequestMethod.POST,
            RequestMethod.PATCH, RequestMethod.HEAD, RequestMethod.DELETE, RequestMethod.GET})
    public Boolean upload(HttpServletRequest servletRequest,
                          HttpServletResponse servletResponse) throws IOException {
        this.tusFileUploadService.process(servletRequest, servletResponse);
        String uploadURI = servletRequest.getRequestURI();
        UploadInfo uploadInfo = null;
        try {
            uploadInfo = this.tusFileUploadService.getUploadInfo(uploadURI);
        } catch (IOException | TusException e) {
            log.error("get upload info", e);
        }
        if (uploadInfo != null && !uploadInfo.isUploadInProgress()) {
            try (InputStream is = this.tusFileUploadService.getUploadedBytes(uploadURI)) {
                return fileService.uploadFile(is);
            } catch (IOException | TusException e) {
                log.error("get uploaded bytes", e);
            }
            try {
                this.tusFileUploadService.deleteUpload(uploadURI);
            } catch (IOException | TusException e) {
                log.error("delete upload", e);
            }
        }
        return false;
    }

    @PostMapping("/new")
    @FilePermissionCheck({
            @FilePermissionCheck.FieldPermission(field = "directory", permission = FilePermissionEnum.UPLOAD)
    })
    public Result fileItem(@Valid @RequestBody UploadFileRequest uploadFileRequest) {
        FileDetail fileDetail = fileItemService.checkUpload(uploadFileRequest.getSha256sum());
        if (fileDetail != null) {
            FileItem fileItem = new FileItem();
            fileItem.setName(uploadFileRequest.getName());
            fileItem.setExt(uploadFileRequest.getExt());
            fileItem.setDirectory(uploadFileRequest.getDirectory());
            fileItem.setUrl(fileDetail.getUrl());
            fileItem.setType(FileTypeEnum.FILE);
            fileItem.setCreatedBy(userService.getUserInfo().getId());
            if (fileItemService.save(fileItem)) {
                return Result.success();
            }
            return Result.error("创建失败");
        } else {
            return Result.error("未找到文件");
        }
    }
}