package com.example.kfile.controller;

import cn.xuyanwu.spring.file.storage.FileStorageService;
import com.example.kfile.domain.FileInfo;
import com.example.kfile.domain.enums.FileTypeEnum;
import com.example.kfile.domain.request.*;
import com.example.kfile.repository.FileInfoRepository;
import com.example.kfile.util.AjaxJson;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * TODO 文件操作相关接口, 如新建文件夹, 上传文件, 删除文件, 移动文件等.
 */
@Api(tags = "文件操作模块")
@Slf4j
@RestController
@RequestMapping("/api/file/operator")
public class FileOperatorController {


    FileStorageService fileStorageService;

    FileInfoRepository fileInfoRepository;

    @Autowired
    public void setFileInfoRepository(FileInfoRepository fileInfoRepository) {
        this.fileInfoRepository = fileInfoRepository;
    }

    @Autowired
    public void setFileStorageService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @ApiOperation(value = "创建文件夹")
    @PostMapping("/mkdir")
    public AjaxJson<?> mkdir(@Valid @RequestBody NewFolderRequest newFolderRequest) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(newFolderRequest.getName());
        fileInfo.setPath(newFolderRequest.getPath());
        fileInfo.setCreatedDate(new Date());
        fileInfo.setType(FileTypeEnum.FOLDER);
        fileInfo.setCreatedBy("");
        if (fileInfoRepository.save(fileInfo) != null) {
            return AjaxJson.getSuccess("创建成功");
        } else {
            return AjaxJson.getError("创建失败");
        }
    }

    public MutableGraph<String> buildMutableGraphByFileId(String fileId) {
        MutableGraph<String> mutableGraph = GraphBuilder.<String>directed().build();
        addChildren(mutableGraph, fileId);
        return mutableGraph;
    }

    public void addChildren(MutableGraph<String> mutableGraph, String fileId) {
        List<FileInfo> fileInfos = fileInfoRepository.findFileInfosByPath(fileId);
        for (FileInfo fileInfo : fileInfos) {
            mutableGraph.addNode(fileInfo.getId());
            if (fileInfo.getType().getValue().equals(FileTypeEnum.FOLDER.getValue())) {
                addChildren(mutableGraph, fileInfo.getId());
            }
            mutableGraph.putEdge(fileId, fileInfo.getId());
        }
    }

    @Transactional
    public Boolean deleteNodeAndDescendants(MutableGraph<String> mutableGraph, String nodeId) {
        boolean canDelete = false;
        Set<String> successors = mutableGraph.successors(nodeId);
        // 后序遍历节点的子节点
        for (String successor : successors) {
            if (deleteNodeAndDescendants(mutableGraph, successor)) {
                canDelete = true;
            }
        }
        if (canDelete) {
            if (fileInfoRepository.findById(nodeId).isPresent()) {
                fileInfoRepository.deleteById(nodeId);
                canDelete = true;
                // 删除节点及其相关边
                mutableGraph.removeNode(nodeId);
            }
        }
        return canDelete;
    }

    @Transactional
    public Boolean copyNodeAndDescendants(MutableGraph<String> mutableGraph, String nodeId, String path) {
        boolean canCopy = false;
        String newNodeValue = "error";
        if (fileInfoRepository.findById(nodeId).isPresent()) {
            FileInfo fileInfo = fileInfoRepository.findById(nodeId).get();
            fileInfo.setId("");
            fileInfo.setShare("");
            fileInfo.setCreatedBy("");
            fileInfo.setCreatedDate(new Date());
            fileInfo.setPath(path);
            fileInfo.setLastModifiedDate(new Date());
            newNodeValue = fileInfoRepository.save(fileInfo).getId();
            modifyNodeValue(mutableGraph, nodeId, newNodeValue);
            canCopy = true;
        }
        Set<String> successors = mutableGraph.successors(newNodeValue);
        if (canCopy) {
            // 先序遍历节点的子节点
            for (String successor : successors) {
                copyNodeAndDescendants(mutableGraph, successor, newNodeValue);
            }
        }
        return canCopy;
    }

    public void modifyNodeValue(MutableGraph<String> mutableGraph, String nodeId, String newNodeValue) {
        // 创建新的节点
        mutableGraph.addNode(newNodeValue);
        // 移除原有节点与其后继节点之间的边
        Set<String> successors = mutableGraph.successors(nodeId);
        for (String successor : successors) {
            mutableGraph.removeEdge(nodeId, successor);
        }
        // 添加新节点与后继节点之间的边
        for (String successor : successors) {
            mutableGraph.putEdge(newNodeValue, successor);
        }
        // 删除原有节点
        mutableGraph.removeNode(nodeId);
    }

    @ApiOperation(value = "删除文件/文件夹")
    @PostMapping("/delete/batch")
    public AjaxJson<?> deleteFile(@Valid @RequestBody String fileId) {
        MutableGraph<String> mutableGraph = buildMutableGraphByFileId(fileId);
        int totalCount = mutableGraph.nodes().size();
        if (deleteNodeAndDescendants(mutableGraph, fileId)) {
            return AjaxJson.getSuccess("删除 " + totalCount + " 个, 删除成功 ");
        } else {
            return AjaxJson.getError("删除 " + totalCount + " 个, " + "删除" + mutableGraph.nodes().size() + "个成功," + "删除" + (totalCount - mutableGraph.nodes().size()) + "个失败");
        }
    }


    @ApiOperation(value = "重命名文件")
    @PostMapping("/rename/file")
    public AjaxJson<?> rename(@Valid @RequestBody RenameFileRequest renameFileRequest) {
        if (fileInfoRepository.findById(renameFileRequest.getFileId()).isPresent()) {
            FileInfo fileInfo = fileInfoRepository.findById(renameFileRequest.getFileId()).get();
            fileInfo.setName(renameFileRequest.getNewName());
            fileInfoRepository.save(fileInfo);
            return AjaxJson.getSuccess("重命名成功");
        } else {
            return AjaxJson.getError("重命名失败");
        }
    }

    @ApiOperation(value = "上传文件")
    @PostMapping("/upload/file")
    public AjaxJson<?> getUploadFileUrl(@Valid @RequestBody UploadFileRequest uploadFileRequest, @RequestPart("file") MultipartFile file) {
        fileStorageService.of(file)
                .setPlatform(uploadFileRequest.getPlatform())    // 使用指定的存储平台
                .upload();
        FileInfo fileInfo = new FileInfo();
        fileInfo.setStorage(uploadFileRequest.getPlatform());
        fileInfo.setSize(file.getSize());
        fileInfo.setName(uploadFileRequest.getName());
        fileInfo.setPath(uploadFileRequest.getPath());
        fileInfo.setCreatedDate(new Date());
        fileInfo.setType(FileTypeEnum.FILE);
        fileInfo.setCreatedBy("");
        fileInfoRepository.save(fileInfo);
        return AjaxJson.getSuccess("上传成功");
    }


    @ApiOperation(value = "移动文件")
    @PostMapping("/move/file")
    public AjaxJson<?> moveFile(@Valid @RequestBody MoveFileRequest moveFileRequest) {
        if (moveFileRequest.getSource().equals(moveFileRequest.getTarget())) {
            return AjaxJson.getError("不能移动到自己!");
        }
        FileInfo fileInfo = fileInfoRepository.findById(moveFileRequest.getFileId()).get();
        fileInfo.setPath(moveFileRequest.getTarget());
        return AjaxJson.getSuccess("移动成功");
    }

    @ApiOperation(value = "复制文件")
    @PostMapping("/copy/file")
    public AjaxJson<?> copyFile(@Valid @RequestBody CopyFileRequest copyFileRequest) {
        MutableGraph<String> mutableGraph = buildMutableGraphByFileId(copyFileRequest.getFileId());
        int totalCount = mutableGraph.nodes().size();
        if (copyNodeAndDescendants(mutableGraph, copyFileRequest.getFileId(), copyFileRequest.getTarget())) {
            return AjaxJson.getSuccess("复制" + totalCount + "个成功");
        } else {
            return AjaxJson.getError("复制失败");
        }
    }
}