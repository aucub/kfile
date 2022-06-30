package com.example.kfile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.kfile.entity.Acl;
import com.example.kfile.entity.FileDetail;
import com.example.kfile.entity.FileItem;
import com.example.kfile.entity.Share;
import com.example.kfile.entity.enums.*;
import com.example.kfile.entity.request.FileListRequest;
import com.example.kfile.entity.request.UploadFileRequest;
import com.example.kfile.entity.result.FileEntry;
import com.example.kfile.mapper.FileItemMapper;
import com.example.kfile.service.FileDetailService;
import com.example.kfile.service.IFileItemService;
import com.example.kfile.service.IShareService;
import com.example.kfile.service.IUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.util.ArrayList;
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
@Slf4j
@Service
public class FileItemServiceImpl extends ServiceImpl<FileItemMapper, FileItem> implements IFileItemService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    FileItemMapper fileItemMapper;
    FileDetailService fileDetailService;
    IUserService userService;

    @Autowired
    IShareService shareService;

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
    public List<FileEntry> list(FileListRequest fileListRequest) {
        QueryWrapper<FileItem> queryWrapper = new QueryWrapper<>();
        // 检查文件项ID是否为空
        if (fileListRequest.getDirectory().isEmpty()) {
            queryWrapper = queryWrapper.eq("created_by", userService.getUserInfo().getId());
        } else {
            queryWrapper = queryWrapper.eq("directory", fileListRequest.getDirectory());
        }
        if (fileListRequest.getOrderBy() == OrderByTypeEnum.TIME) {
            if (fileListRequest.getOrderDirection() == OrderDirectionTypeEnum.DESC) {
                queryWrapper = queryWrapper.orderByDesc("last_modified_date");
            } else {
                queryWrapper = queryWrapper.orderByAsc("last_modified_date");
            }
        }
        if (fileListRequest.getOrderBy() == OrderByTypeEnum.NAME) {
            if (fileListRequest.getOrderDirection() == OrderDirectionTypeEnum.DESC) {
                queryWrapper = queryWrapper.orderByDesc("name");
            } else {
                queryWrapper = queryWrapper.orderByAsc("name");
            }
        }
        // 查询文件项列表
        List<FileItem> fileItems = fileItemMapper.selectList(queryWrapper);
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
        FileListRequest fileListRequest = new FileListRequest();
        fileListRequest.setDirectory(directory);
        for (FileEntry fileEntry : list(fileListRequest)) {
            if (fileEntry.getName().equals(name)) {
                return false;
            }
        }
        FileItem fileItem = new FileItem();
        fileItem.setName(name);
        fileItem.setDirectory(directory);
        fileItem.setType(FileTypeEnum.FOLDER);
        fileItem.setCreatedBy(userService.getUserInfo().getId());
        return save(fileItem);
    }

    @Override
    public Boolean newFile(UploadFileRequest uploadFileRequest) {
        FileDetail fileDetail = checkUpload(uploadFileRequest.getSha256sum());
        if (fileDetail != null) {
            FileItem fileItem = new FileItem();
            fileItem.setUrl(fileDetail.getUrl());
            fileItem.setName(uploadFileRequest.getName());
            fileItem.setType(FileTypeEnum.FILE);
            fileItem.setDirectory(uploadFileRequest.getDirectory());
            fileItem.setCreatedBy(userService.getUserInfo().getId());
            return save(fileItem);
        }
        return false;
    }

    @Override
    public FileDetail checkUpload(String sha256sum) {
        return fileDetailService.getOne(new QueryWrapper<FileDetail>().eq("sha256sum", sha256sum));
    }

    @Override
    public FilePermissionEnum getPermission(String id) {
        FileItem fileItem = getById(id);
        if (fileItem.getCreatedBy().equals(userService.getUserInfo().getId())) {
            return FilePermissionEnum.OWNER;
        } else if (fileItem.getShare() != null) {
            Share share = shareService.getById(fileItem.getShare());
            if (share.getAcl().equals(AclEnum.USER)) {
                try {
                    CollectionType type = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, Acl.class);
                    List<Acl> aclList = OBJECT_MAPPER.readValue(share.getAclList(), type);
                    if (aclList.get(0) != null) return aclList.get(0).getPermission();
                } catch (JsonProcessingException e) {
                    log.error(e.getMessage());
                }
            }
            if (share.getAcl().equals(AclEnum.ACL)) {
                try {
                    CollectionType type = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, Acl.class);
                    List<Acl> aclList = OBJECT_MAPPER.readValue(share.getAclList(), type);
                    for (Acl acl : aclList) {
                        if (acl.getId().toString().equals(id)) {
                            return acl.getPermission();
                        }
                    }
                } catch (JsonProcessingException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return null;
    }

    @Override
    @Transactional
    // 删除文件
    public Boolean deleteFile(String fileItemId) {
        MutableGraph<String> mutableGraph = buildMutableGraphByFileId(fileItemId);
        return Boolean.TRUE.equals(deleteNodeAndDescendants(mutableGraph, fileItemId));
    }

    @Override
    @Transactional
    // 复制文件
    public Boolean copyFile(String fileItemId, String targetDirectory) {
        // 根据文件ID建可变图
        MutableGraph<String> mutableGraph = buildMutableGraphByFileId(fileItemId);
        // 复制节点及其子节点到目标目录
        return !copyNodeAndDescendants(mutableGraph, fileItemId, targetDirectory).isEmpty();
    }

    @Override
    public Boolean moveFile(String fileItemId, String targetDirectory) {
        // 验证输入参数是否为空或不符合要求的格式
        if (fileItemId == null || fileItemId.isEmpty() || targetDirectory == null || targetDirectory.isEmpty()) {
            return false;
        }
        // 检查文件是否存在
        FileItem fileItem = getById(fileItemId);
        if (fileItem == null || fileItem.getDirectory().equals(targetDirectory) || fileItem.getDirectory().isEmpty()) {
            return false;
        }
        // 更新文件目录
        fileItem.setDirectory(targetDirectory);
        return updateById(fileItem);
    }

    @Override
    /*
      重命名文件
      @param fileItemId 文件项ID
     * @param newName 新文件名
     * @return 重命名后的文件项
     * @throws FileNotFoundException 如果找不到文件信息
     */
    public Boolean renameFile(String fileItemId, String newName) {
        FileItem fileItem = getById(fileItemId);
        FileListRequest fileListRequest = new FileListRequest();
        fileListRequest.setDirectory(fileItem.getDirectory());
        // 检查文件项是否存在
        for (FileEntry fileEntry : list(fileListRequest)) {
            if (fileEntry.getName().equals(newName)) {
                return false;
            }
        }
        return updateById(fileItem);
    }

    // 获取文件的所有者
    public Integer getOwner(String fileItemId) {
        FileItem fileItem = getById(fileItemId);
        return fileItem.getCreatedBy();
    }

    public MutableGraph<String> buildMutableGraphByFileId(String fileItemId) {
        MutableGraph<String> mutableGraph = GraphBuilder.directed().build();
        FileItem fileItem = getById(fileItemId);
        if (fileItem.getType().equals(FileTypeEnum.FOLDER)) {
            addChildren(mutableGraph, fileItemId);
        }
        return mutableGraph;
    }

    public void addChildren(MutableGraph<String> mutableGraph, String fileItemId) {
        QueryWrapper<FileItem> queryWrapper = new QueryWrapper<FileItem>().eq("directory", fileItemId);
        List<FileItem> fileItems = fileItemMapper.selectList(queryWrapper);
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
        boolean canDelete = true;
        Set<String> successors = mutableGraph.successors(nodeId);
        for (String successor : successors) {
            if (!Boolean.TRUE.equals(deleteNodeAndDescendants(mutableGraph, successor))) {
                canDelete = false;
            }
        }
        if (canDelete && getById(nodeId) != null) {
            fileItemMapper.deleteById(nodeId);
            mutableGraph.removeNode(nodeId);
        }
        return canDelete;
    }

    @Transactional
    public String copyNodeAndDescendants(MutableGraph<String> mutableGraph, String nodeId, String directory) {
        String newNodeValue;
        FileItem fileItem = getById(nodeId);
        fileItem.setCreatedBy(userService.getUserInfo().getId());
        fileItem.setDirectory(directory);
        fileItemMapper.insert(fileItem);
        newNodeValue = fileItem.getId();
        modifyNodeValue(mutableGraph, nodeId, newNodeValue);
        Set<String> successors = mutableGraph.successors(newNodeValue);
        // 先序遍历节点的子节点
        for (String successor : successors) {
            copyNodeAndDescendants(mutableGraph, successor, newNodeValue);
        }
        return newNodeValue;
    }

    public void modifyNodeValue(MutableGraph<String> mutableGraph, String nodeId, String newNodeValue) {
        mutableGraph.addNode(newNodeValue);
        Set<String> successors = mutableGraph.successors(nodeId);
        for (String successor : successors) {
            mutableGraph.removeEdge(nodeId, successor);
        }
        for (String successor : successors) {
            mutableGraph.putEdge(newNodeValue, successor);
        }
        mutableGraph.removeNode(nodeId);
    }

}
