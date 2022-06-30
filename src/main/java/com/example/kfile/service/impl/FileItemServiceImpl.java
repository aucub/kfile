package com.example.kfile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.kfile.entity.FileDetail;
import com.example.kfile.entity.FileItem;
import com.example.kfile.entity.enums.FileTypeEnum;
import com.example.kfile.entity.request.UploadFileRequest;
import com.example.kfile.entity.result.FileEntry;
import com.example.kfile.mapper.FileItemMapper;
import com.example.kfile.service.FileDetailService;
import com.example.kfile.service.IFileItemService;
import com.example.kfile.service.IUserService;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import org.dromara.x.file.storage.core.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author aucub
 * @since 2023-11-12
 */
@Service
public class FileItemServiceImpl extends ServiceImpl<FileItemMapper, FileItem> implements IFileItemService {

    FileItemMapper fileItemMapper;
    FileDetailService fileDetailService;
    IUserService userService;

    @Autowired
    public void setFileItemMapper(FileItemMapper fileItemMapper) {
        this.fileItemMapper = fileItemMapper;
    }

    @Autowired
    public void setFileDetailService(FileDetailService fileDetailService) {
        this.fileDetailService = fileDetailService;
    }

    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public List<FileEntry> fileList(String fileItemId) {
        // 检查文件项ID是否为空
        if (fileItemId == null || fileItemId.isEmpty()) {
            throw new IllegalArgumentException("文件项ID不能为空");
        }
        // 查询文件项列表
        List<FileItem> fileItems = fileItemMapper.findFileItemByDirectory(fileItemId);
        // 创建文件列表
        List<FileEntry> fileEntries = new ArrayList<>();
        // 遍历文件项列表
        for (FileItem fileItem : fileItems) {
            FileEntry fileEntry = BeanUtil.copyProperties(fileItem, FileEntry.class);
            if (fileItem.getType().getValue().equals(FileTypeEnum.FILE.getValue())) {
                // 根据文件信息ID查询文件详情
                FileInfo fileInfo = fileDetailService.getByUrl(fileItem.getUrl());

                fileEntry.setSize(fileInfo.getSize());
                fileEntry.setSha256sum(fileInfo.getAttr().getStr("sha256sum"));

            }
            fileEntries.add(fileEntry);
        }
        // 返回文件列表
        return fileEntries;
    }

    @Override
    public FileEntry getFileEntry(String fileItemId) throws FileNotFoundException {
        // 验证文件项ID是否为空
        if (fileItemId == null || fileItemId.isEmpty()) {
            throw new FileNotFoundException("文件项ID不能为空");
        }
        // 根据文件项ID查找文件项
        FileItem fileItem = fileItemMapper.selectById(fileItemId);
        FileEntry fileEntry = BeanUtil.copyProperties(fileItem, FileEntry.class);
        // 根据文件项中的文件信息ID查找文件详情
        FileInfo fileInfo = fileDetailService.getByUrl(fileItem.getUrl());

        fileEntry.setSize(fileInfo.getSize());
        fileEntry.setSha256sum(fileInfo.getAttr().getStr("sha256sum"));
        return fileEntry;
    }

    @Override
    public Boolean newFolder(String directory, String name) {
        for (FileEntry fileEntry : fileList(directory)) {
            if (fileEntry.getName().equals(name)) {
                return false;
            }
        }
        FileItem fileItem = new FileItem();
        fileItem.setName(name);
        fileItem.setDirectory(directory);
        fileItem.setCreatedDate(new Date());
        fileItem.setType(FileTypeEnum.FOLDER);
        fileItem.setCreatedBy(userService.getUserInfo().getId());
        return save(fileItem);
    }

    @Override
    public Boolean newFile(UploadFileRequest uploadFileRequest, FileDetail fileDetail) {
        FileItem fileItem = new FileItem();
        fileItem.setUrl(fileDetail.getUrl());
        fileItem.setName(uploadFileRequest.getName());
        fileItem.setType(FileTypeEnum.FILE);
        fileItem.setDirectory(uploadFileRequest.getDirectory());
        fileItem.setCreatedBy(userService.getUserInfo().getId());
        fileItem.setCreatedDate(new Date());
        fileItem.setLastModifiedDate(new Date());
        return save(fileItem);
    }

    @Override
    public FileDetail checkUpload(UploadFileRequest uploadFileRequest) {
        FileDetail fileDetail = fileDetailService.getOne(new QueryWrapper<FileDetail>().eq("sha256sum", uploadFileRequest.getSha256sum()));
        return fileDetail;
    }

    @Override
    @Transactional
    // 删除文件
    public String deleteFile(String fileItemId) throws FileNotFoundException {
        // 根据文件ID构建可变图
        MutableGraph<String> mutableGraph = buildMutableGraphByFileId(fileItemId);
        // 获取图中节点的总数
        int totalCount = mutableGraph.nodes().size();
        // 删除节点及其子节点，并返回是否删除成功
        if (Boolean.TRUE.equals(deleteNodeAndDescendants(mutableGraph, fileItemId))) {
            // 如果删除成功，返回删除成功的消息
            return "删除成功：" + totalCount + "个";
        } else {
            // 如果删除失败，计算成功删除的节点数量和失败删除的节点数量
            int successCount = mutableGraph.nodes().size();
            int failureCount = totalCount - successCount;
            // 返回删除失败和成功的消息
            return "删除失败：" + failureCount + "个，删除成功：" + successCount + "个";
        }
    }

    @Override
    @Transactional
    // 复制文件
    public FileEntry copyFile(String fileItemId, String targetDirectory) throws FileNotFoundException {
        // 根据文件ID构建可变图
        MutableGraph<String> mutableGraph = buildMutableGraphByFileId(fileItemId);
        // 复制节点及其子节点到目标目录
        String newFileItemId = copyNodeAndDescendants(mutableGraph, fileItemId, targetDirectory);
        // 根据新的文件ID获取新的文件项
        FileItem newFileItem = getById(newFileItemId);
        FileInfo fileInfo = fileDetailService.getByUrl(newFileItem.getUrl());
        FileEntry fileEntry = BeanUtil.copyProperties(newFileItem, FileEntry.class);
        fileEntry.setSize(fileInfo.getSize());
        fileEntry.setSha256sum(fileInfo.getAttr().getStr("sha256sum"));
        // 返回新的文件项
        return fileEntry;
    }

    @Override
    public FileEntry moveFile(String fileItemId, String targetDirectory) {
        // 验证输入参数是否为空或不符合要求的格式
        if (fileItemId == null || fileItemId.isEmpty() || targetDirectory == null || targetDirectory.isEmpty()) {
            throw new IllegalArgumentException("无效的参数!");
        }
        // 检查文件是否存在
        FileItem fileItem = getById(fileItemId);
        // 检查目标目录是否与当前目录相同
        if (fileItem.getDirectory().equals(targetDirectory)) {
            throw new IllegalArgumentException("不能移动到自己!");
        }
        // 更新文件目录
        fileItem.setDirectory(targetDirectory);
        save(fileItem);

        // 返回新的文件项
        FileEntry fileEntry = BeanUtil.copyProperties(fileItem, FileEntry.class);
        FileInfo fileInfo = fileDetailService.getByUrl(fileItem.getUrl());
        fileEntry.setSize(fileInfo.getSize());
        fileEntry.setSha256sum(fileInfo.getAttr().getStr("sha256sum"));
        return fileEntry;
    }

    @Override
    /*
      重命名文件
      @param fileItemId 文件项ID
     * @param newName 新文件名
     * @return 重命名后的文件项
     * @throws FileNotFoundException 如果找不到文件信息
     */
    public Boolean renameFile(String fileItemId, String newName) throws FileNotFoundException {
        FileItem fileItem = getById(fileItemId);
        // 检查文件项是否存在
        for (FileEntry fileEntry : fileList(fileItem.getDirectory())) {
            if (fileEntry.getName().equals(newName)) {
                return false;
            }
        }
        return save(fileItem);
    }

    // 获取文件的所有者
    public Integer getOwner(String fileItemId) {
        FileItem fileItem = getById(fileItemId);
        return fileItem.getCreatedBy();
    }

    public MutableGraph<String> buildMutableGraphByFileId(String fileItemId) throws FileNotFoundException {
        MutableGraph<String> mutableGraph = GraphBuilder.directed().build();
        FileItem fileItem = getById(fileItemId);
        if (fileItem.getType().equals(FileTypeEnum.FOLDER)) {
            addChildren(mutableGraph, fileItemId);
        }
        return mutableGraph;
    }

    public void addChildren(MutableGraph<String> mutableGraph, String fileItemId) {
        List<FileItem> fileItems = fileItemMapper.findFileItemByDirectory(fileItemId);
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
            if (Boolean.TRUE.equals(deleteNodeAndDescendants(mutableGraph, successor))) {
                canDelete = true;
            }
        }
        if (canDelete && getById(nodeId) != null) {
            fileItemMapper.deleteById(nodeId);
            // 删除节点及其相关边
            mutableGraph.removeNode(nodeId);

        }
        return canDelete;
    }

    @Transactional
    public String copyNodeAndDescendants(MutableGraph<String> mutableGraph, String nodeId, String directory) {
        boolean canCopy = false;
        String newNodeValue = "null";
        FileItem fileItem = getById(nodeId);
        fileItem.setCreatedBy(userService.getUserInfo().getId());
        fileItem.setCreatedDate(new Date());
        fileItem.setDirectory(directory);
        fileItem.setLastModifiedDate(new Date());
        save(fileItem);
        newNodeValue = fileItem.getId();
        modifyNodeValue(mutableGraph, nodeId, newNodeValue);
        canCopy = true;
        Set<String> successors = mutableGraph.successors(newNodeValue);
        if (canCopy) {
            // 先序遍历节点的子节点
            for (String successor : successors) {
                copyNodeAndDescendants(mutableGraph, successor, newNodeValue);
            }
        }
        return newNodeValue;
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

}
