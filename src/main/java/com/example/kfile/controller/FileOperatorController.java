package com.example.kfile.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.FileStorageService;
import com.example.kfile.domain.FileDetail;
import com.example.kfile.domain.FileItem;
import com.example.kfile.domain.enums.FileTypeEnum;
import com.example.kfile.domain.request.*;
import com.example.kfile.exception.StorageSourceFileOperatorException;
import com.example.kfile.repository.FileDetailRepository;
import com.example.kfile.repository.FileItemRepository;
import com.example.kfile.util.AjaxJson;
import com.example.kfile.util.CodeMsg;
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

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 文件操作相关接口, 如新建文件夹, 上传文件, 删除文件, 移动文件等.
 */
@Api(tags = "文件操作模块")
@Slf4j
@RestController
@RequestMapping("/api/file/operator")
public class FileOperatorController {


    FileStorageService fileStorageService;

    FileDetailRepository fileDetailRepository;
    FileItemRepository fileItemRepository;

    @Autowired
    public void setFileDetailRepository(FileDetailRepository fileDetailRepository) {
        this.fileDetailRepository = fileDetailRepository;
    }

    @Autowired
    public void setFileItemRepository(FileItemRepository fileItemRepository) {
        this.fileItemRepository = fileItemRepository;
    }

    @Autowired
    public void setFileStorageService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @ApiOperation(value = "创建文件夹")
    @PostMapping("/mkdir")
    public AjaxJson<?> mkdir(@Valid @RequestBody NewFolderRequest newFolderRequest) {
        FileItem fileItem = new FileItem();
        fileItem.setName(newFolderRequest.getName());
        fileItem.setDirectory(newFolderRequest.getDirectory());
        fileItem.setCreatedDate(new Date());
        fileItem.setType(FileTypeEnum.FOLDER);
        fileItem.setCreatedBy(StpUtil.getLoginId().toString());
        fileItemRepository.save(fileItem);
        return AjaxJson.getSuccess("创建成功");
    }

    public MutableGraph<String> buildMutableGraphByFileId(String fileItemId) throws FileNotFoundException {
        MutableGraph<String> mutableGraph = GraphBuilder.directed().build();
        Optional<FileItem> fileItemOptional = fileItemRepository.findById(fileItemId);
        if (fileItemOptional.isPresent()) {
            FileItem fileItem = fileItemOptional.get();
            if (fileItem.getType().equals(FileTypeEnum.FOLDER)) {
                addChildren(mutableGraph, fileItemId);
            }
        } else {
            throw new FileNotFoundException("找不到文件信息：" + fileItemId);
        }
        return mutableGraph;
    }

    public void addChildren(MutableGraph<String> mutableGraph, String fileItemId) {
        List<FileItem> fileItems = fileItemRepository.findFileItemByDirectory(fileItemId);
        for (FileItem fileItem : fileItems) {
            mutableGraph.addNode(fileItem.getId());
            if (fileItem.getType().getValue().equals(FileTypeEnum.FOLDER.getValue())) {
                addChildren(mutableGraph, fileItem.getId());
            }
            mutableGraph.putEdge(fileItemId, fileItem.getId());
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
            if (fileDetailRepository.findById(nodeId).isPresent()) {
                fileDetailRepository.deleteById(nodeId);
                // 删除节点及其相关边
                mutableGraph.removeNode(nodeId);
            }
        }
        return canDelete;
    }

    @Transactional
    public Boolean copyNodeAndDescendants(MutableGraph<String> mutableGraph, String nodeId, String directory) {
        boolean canCopy = false;
        String newNodeValue = "null";
        if (fileItemRepository.findById(nodeId).isPresent()) {
            FileItem fileItem = fileItemRepository.findById(nodeId).get();
            fileItem.setCreatedBy(StpUtil.getLoginId().toString());
            fileItem.setCreatedDate(new Date());
            fileItem.setDirectory(directory);
            fileItem.setLastModifiedDate(new Date());
            newNodeValue = fileItemRepository.save(fileItem).getId();
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
    @Transactional
    @PostMapping("/delete/batch")
    public AjaxJson<?> deleteFile(@Valid @RequestBody String fileItemId) throws FileNotFoundException {
        MutableGraph<String> mutableGraph = buildMutableGraphByFileId(fileItemId);
        int totalCount = mutableGraph.nodes().size();
        if (deleteNodeAndDescendants(mutableGraph, fileItemId)) {
            return AjaxJson.getSuccess("删除 " + totalCount + " 个, 删除成功 ");
        } else {
            return AjaxJson.getError("删除 " + totalCount + " 个, " + "删除" + mutableGraph.nodes().size() + "个成功," + "删除" + (totalCount - mutableGraph.nodes().size()) + "个失败");
        }
    }


    @ApiOperation(value = "重命名文件")
    @PostMapping("/rename/file")
    public AjaxJson<?> rename(@Valid @RequestBody RenameFileRequest renameFileRequest) throws FileNotFoundException {
        if (fileItemRepository.findById(renameFileRequest.getFileId()).isPresent()) {
            FileItem fileItem = fileItemRepository.findById(renameFileRequest.getFileId()).get();
            fileItem.setName(renameFileRequest.getNewName());
            fileItemRepository.save(fileItem);
            return AjaxJson.getSuccess("重命名成功");
        } else {
            throw new FileNotFoundException("找不到文件信息：" + renameFileRequest.getFileId());
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
        if (moveFileRequest.getSource().equals(moveFileRequest.getTarget())) {
            return AjaxJson.getError("不能移动到自己!");
        }
        Optional<FileItem> fileItemOptional = fileItemRepository.findById(moveFileRequest.getFileItemId());
        if (fileItemOptional.isEmpty()) {
            throw new IllegalArgumentException("找不到文件信息:" + moveFileRequest.getFileItemId());
        }
        FileItem fileItem = fileItemOptional.get();
        fileItem.setDirectory(moveFileRequest.getTarget());
        return AjaxJson.getSuccess("移动成功");
    }

    @ApiOperation(value = "复制文件")
    @Transactional
    @PostMapping("/copy/file")
    public AjaxJson<?> copyFile(@Valid @RequestBody CopyFileRequest copyFileRequest) throws FileNotFoundException {
        MutableGraph<String> mutableGraph = buildMutableGraphByFileId(copyFileRequest.getFileItemId());
        int totalCount = mutableGraph.nodes().size();
        if (copyNodeAndDescendants(mutableGraph, copyFileRequest.getFileItemId(), copyFileRequest.getTarget())) {
            return AjaxJson.getSuccess("复制" + totalCount + "个成功");
        } else {
            return AjaxJson.getError("复制失败");
        }
    }
}