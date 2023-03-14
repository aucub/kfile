package com.example.kfile.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.example.kfile.domain.FileDetail;
import com.example.kfile.domain.FileItem;
import com.example.kfile.domain.SandBox;
import com.example.kfile.domain.enums.FileTypeEnum;
import com.example.kfile.domain.request.UploadFileRequest;
import com.example.kfile.domain.result.FileEntry;
import com.example.kfile.exception.ServiceException;
import com.example.kfile.repository.FileDetailRepository;
import com.example.kfile.repository.FileItemRepository;
import com.example.kfile.repository.SandBoxRepository;
import com.example.kfile.service.FileService;
import com.example.kfile.util.CodeMsg;
import com.example.kfile.util.FileUtil;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.util.*;

@SuppressWarnings("UnstableApiUsage")
@Service
public class FileServiceImpl implements FileService {

    FileItemRepository fileItemRepository;
    FileDetailRepository fileDetailRepository;
    SandBoxRepository sandBoxRepository;

    @Autowired
    public void setFileItemRepository(FileItemRepository fileItemRepository) {
        this.fileItemRepository = fileItemRepository;
    }

    @Autowired
    public void setFileDetailRepository(FileDetailRepository fileDetailRepository) {
        this.fileDetailRepository = fileDetailRepository;
    }

    @Autowired
    public void setSandBoxRepository(SandBoxRepository sandBoxRepository) {
        this.sandBoxRepository = sandBoxRepository;
    }

    @Override
    public List<FileEntry> fileList(String fileItemId) throws Exception {
        // 检查文件项ID是否为空
        if (fileItemId == null || fileItemId.isEmpty()) {
            throw new IllegalArgumentException("文件项ID不能为空");
        }
        // 查询文件项列表
        List<FileItem> fileItems = fileItemRepository.findFileItemByDirectory(fileItemId);
        // 创建文件列表
        List<FileEntry> fileEntries = new ArrayList<>();
        // 遍历文件项列表
        for (FileItem fileItem : fileItems) {
            // 根据文件信息ID查询文件详情
            FileDetail fileDetail = fileDetailRepository.findById(fileItem.getFileInfoId())
                    .orElseThrow(() -> new FileNotFoundException("找不到文件信息：" + fileItem.getFileInfoId()));
            // 根据文件项和文件详情创建文件条目
            FileEntry fileEntry = FileUtil.getFileEntry(fileItem, fileDetail);
            // 将文件条目添加到文件列表
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
        FileItem fileItem = fileItemRepository.findById(fileItemId)
                .orElseThrow(() -> new FileNotFoundException("找不到文件项：" + fileItemId));
        // 根据文件项中的文件信息ID查找文件详情
        Optional<FileDetail> fileDetailOptional = fileDetailRepository.findById(fileItem.getFileInfoId());
        if (fileDetailOptional.isPresent()) {
            // 如果文件详情存在，则获取文件条目并返回文件条目
            FileDetail fileDetail = fileDetailOptional.get();
            return FileUtil.getFileEntry(fileItem, fileDetail);
        } else {
            // 如果文件详情不存在，则抛出文件未找到异常
            throw new FileNotFoundException("找不到文件信息：" + fileItem.getFileInfoId());
        }
    }

    @Override
    public FileEntry newFolder(String directory, String name) {
        FileItem fileItem = new FileItem();
        fileItem.setName(name);
        fileItem.setDirectory(directory);
        fileItem.setCreatedDate(new Date());
        fileItem.setType(FileTypeEnum.FOLDER);
        fileItem.setCreatedBy(StpUtil.getLoginId().toString());
        return (FileEntry) fileItemRepository.save(fileItem);
    }

    @Override
    public FileItem newFile(UploadFileRequest uploadFileRequest, FileDetail fileDetail) {
        FileItem fileItem = new FileItem();
        fileItem.setVersion((short) 0);
        fileItem.setFileInfoId(fileDetail.getId());
        fileItem.setName(uploadFileRequest.getName());
        fileItem.setType(FileTypeEnum.FILE);
        fileItem.setDirectory(uploadFileRequest.getDirectory());
        fileItem.setCreatedBy(StpUtil.getLoginId().toString());
        fileItem.setCreatedDate(new Date());
        fileItem.setLastModifiedDate(new Date());
        return fileItemRepository.save(fileItem);
    }

    @Override
    public FileDetail checkUpload(UploadFileRequest uploadFileRequest) {
        FileDetail fileDetail = fileDetailRepository.findFileDetailBySha256sum(uploadFileRequest.getSha256sum());
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
        Optional<FileItem> newFileItem = fileItemRepository.findById(newFileItemId);
        // 如果新的文件项存在
        if (newFileItem.isPresent()) {
            FileItem fileItem = newFileItem.get();
            // 根据文件项的文件信息ID获取文件详情
            Optional<FileDetail> fileDetail = fileDetailRepository.findById(fileItem.getFileInfoId());
            // 如果文件详情存在，则返回文件条目
            if (fileDetail.isPresent()) {
                return FileUtil.getFileEntry(fileItem, fileDetail.get());
            } else {
                // 如果文件详情不存在，则抛出异常
                throw new ServiceException(CodeMsg.STORAGE_SOURCE_FILE_COPY_FAIL);
            }
        } else {
            // 如果新的文件项不存在，则抛出异常
            throw new ServiceException(CodeMsg.STORAGE_SOURCE_FILE_COPY_FAIL);
        }
    }

    @Override
    public FileEntry moveFile(String fileItemId, String targetDirectory) {
        // 验证输入参数是否为空或不符合要求的格式
        if (fileItemId == null || fileItemId.isEmpty() || targetDirectory == null || targetDirectory.isEmpty()) {
            throw new IllegalArgumentException("无效的参数!");
        }
        // 检查文件是否存在
        Optional<FileItem> fileItemOptional = fileItemRepository.findById(fileItemId);
        if (fileItemOptional.isEmpty()) {
            throw new IllegalArgumentException("找不到文件信息:" + fileItemId);
        }
        FileItem fileItem = fileItemOptional.get();
        // 检查目标目录是否与当前目录相同
        if (fileItem.getDirectory().equals(targetDirectory)) {
            throw new IllegalArgumentException("不能移动到自己!");
        }
        // 更新文件目录
        fileItem.setDirectory(targetDirectory);
        fileItemRepository.save(fileItem);
        // 获取文件详细信息
        FileDetail fileDetail = fileDetailRepository.findById(fileItem.getFileInfoId()).orElse(null);
        if (fileDetail == null) {
            throw new IllegalArgumentException("找不到文件详细信息:" + fileItem.getFileInfoId());
        }
        // 返回文件信息
        return FileUtil.getFileEntry(fileItem, fileDetail);
    }

    @Override
    /*
      重命名文件
      @param fileItemId 文件项ID
     * @param newName 新文件名
     * @return 重命名后的文件项
     * @throws FileNotFoundException 如果找不到文件信息
     */
    public FileEntry renameFile(String fileItemId, String newName) throws FileNotFoundException {
        // 检查文件项是否存在
        if (fileItemRepository.findById(fileItemId).isPresent()) {
            // 获取文件项
            FileItem fileItem = fileItemRepository.findById(fileItemId).get();
            // 设置新文件名
            fileItem.setName(newName);
            // 保存文件项
            fileItem = fileItemRepository.save(fileItem);
            // 检查文件名是否已更新成功
            if (fileItem.getName().equals(newName) && fileDetailRepository.findById(fileItem.getFileInfoId()).isPresent()) {
                // 获取文件项的文件详情
                return FileUtil.getFileEntry(fileItem, fileDetailRepository.findById(fileItem.getFileInfoId()).get());
            }
            // 抛出文件重命名失败的异常
            throw new ServiceException(CodeMsg.STORAGE_SOURCE_FILE_RENAME_FAIL);
        } else {
            // 抛出找不到文件信息的异常
            throw new FileNotFoundException("找不到文件信息：" + fileItemId);
        }
    }

    // 获取文件的所有者
    public String getOwner(String fileId) {
        Optional<FileDetail> fileDetailOptional = fileDetailRepository.findById(fileId);
        // 检查文件详情是否存在或路径是否为空
        if (fileDetailOptional.isEmpty() || fileDetailOptional.get().getPath() == null || fileDetailOptional.get().getPath().isEmpty()) {
            Optional<SandBox> sandBoxOptional = sandBoxRepository.findById(fileId);
            // 检查沙盒是否存在
            if (sandBoxOptional.isPresent()) {
                return sandBoxOptional.get().getOwner();
            } else {
                throw new IllegalArgumentException("Invalid fileId");
            }
        } else {
            return getOwner(fileDetailOptional.get().getPath());
        }
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
            if (Boolean.TRUE.equals(deleteNodeAndDescendants(mutableGraph, successor))) {
                canDelete = true;
            }
        }
        if (canDelete && (fileDetailRepository.findById(nodeId).isPresent())) {
            fileDetailRepository.deleteById(nodeId);
            // 删除节点及其相关边
            mutableGraph.removeNode(nodeId);

        }
        return canDelete;
    }

    @Transactional
    public String copyNodeAndDescendants(MutableGraph<String> mutableGraph, String nodeId, String directory) {
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
