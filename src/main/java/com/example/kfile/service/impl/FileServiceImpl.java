package com.example.kfile.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.example.kfile.domain.FileDetail;
import com.example.kfile.domain.FileItem;
import com.example.kfile.domain.ShareLink;
import com.example.kfile.domain.enums.FileTypeEnum;
import com.example.kfile.domain.result.FileEntry;
import com.example.kfile.exception.ServiceException;
import com.example.kfile.repository.FileDetailRepository;
import com.example.kfile.repository.FileItemRepository;
import com.example.kfile.repository.SandBoxRepository;
import com.example.kfile.repository.ShareLinkRepository;
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

@Service
public class FileServiceImpl implements FileService {

    FileItemRepository fileItemRepository;
    FileDetailRepository fileDetailRepository;
    SandBoxRepository sandBoxRepository;
    ShareLinkRepository shareLinkRepository;

    @Autowired
    public void setFileInfoRepository(FileDetailRepository fileInfoRepository) {
        this.fileDetailRepository = fileInfoRepository;
    }

    @Autowired
    public void setSandBoxRepository(SandBoxRepository sandBoxRepository) {
        this.sandBoxRepository = sandBoxRepository;
    }

    @Autowired
    public void setShareLinkRepository(ShareLinkRepository shareLinkRepository) {
        this.shareLinkRepository = shareLinkRepository;
    }

    @Override
    public List<FileEntry> fileList(String fileItemId) throws Exception {
        List<FileItem> fileItems = fileItemRepository.findFileItemByDirectory(fileItemId);
        List<FileEntry> fileEntries = new ArrayList<>();
        for (FileItem fileItem : fileItems) {
            FileDetail fileDetail = fileDetailRepository.findById(fileItem.getFileInfoId()).orElseThrow(() -> new FileNotFoundException("找不到文件信息：" + fileItem.getFileInfoId()));
            fileEntries.add(FileUtil.getFileEntry(fileItem, fileDetail));
        }
        return fileEntries;
    }

    @Override
    public FileEntry getFileEntry(String fileItemId) throws FileNotFoundException {
        FileItem fileItem = fileItemRepository.findById(fileItemId).orElseThrow(FileNotFoundException::new);
        Optional<FileDetail> fileDetailOptional = fileDetailRepository.findById(fileItem.getFileInfoId());
        if (fileDetailOptional.isPresent()) {
            FileDetail fileDetail = fileDetailOptional.get();
            return FileUtil.getFileEntry(fileItem, fileDetail);
        } else {
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
    public String deleteFile(String fileItemId) throws FileNotFoundException {
        MutableGraph<String> mutableGraph = buildMutableGraphByFileId(fileItemId);
        int totalCount = mutableGraph.nodes().size();
        if (deleteNodeAndDescendants(mutableGraph, fileItemId)) {
            return "删除 " + totalCount + " 个, 删除成功 ";
        } else {
            return "删除 " + totalCount + " 个, " + "删除" + mutableGraph.nodes().size() + "个成功," + "删除" + (totalCount - mutableGraph.nodes().size()) + "个失败";
        }
    }

    @Override
    public FileEntry copyFile(String fileItemId, String targetDirectory) throws FileNotFoundException {
        MutableGraph<String> mutableGraph = buildMutableGraphByFileId(fileItemId);
        int totalCount = mutableGraph.nodes().size();
        if (copyNodeAndDescendants(mutableGraph, fileItemId, targetDirectory)) {
            return FileUtil.getFileEntry(fileItemRepository.findById(fileItemId).get(), fileDetailRepository.findById(fileItemRepository.findById(fileItemId).get().getFileInfoId()).get());
        } else {
            throw new ServiceException(CodeMsg.STORAGE_SOURCE_FILE_COPY_FAIL);
        }
    }

    @Override
    public FileEntry moveFile(String fileItemId, String targetDirectory) {
        if (fileItemRepository.findById(fileItemId).get().getDirectory().equals(targetDirectory)) {
            throw new IllegalArgumentException("不能移动到自己!");
        }
        Optional<FileItem> fileItemOptional = fileItemRepository.findById(fileItemId);
        if (fileItemOptional.isEmpty()) {
            throw new IllegalArgumentException("找不到文件信息:" + fileItemId);
        }
        FileItem fileItem = fileItemOptional.get();
        fileItem.setDirectory(targetDirectory);
        return null;
    }

    @Override
    public FileEntry renameFile(String fileItemId, String newName) throws FileNotFoundException {
        if (fileItemRepository.findById(fileItemId).isPresent()) {
            FileItem fileItem = fileItemRepository.findById(fileItemId).get();
            fileItem.setName(newName);
            fileItem = fileItemRepository.save(fileItem);
            if (fileItem.getName().equals(newName)) {
                return FileUtil.getFileEntry(fileItem, fileDetailRepository.findById(fileItem.getFileInfoId()).get());
            }
            throw new ServiceException(CodeMsg.STORAGE_SOURCE_FILE_RENAME_FAIL);
        } else {
            throw new FileNotFoundException("找不到文件信息：" + fileItemId);
        }
    }

    public String getOwner(String fileId) {
        if (fileDetailRepository.findById(fileId).get().getPath() == null || fileDetailRepository.findById(fileId).get().getPath().equals("")) {
            return sandBoxRepository.findById(fileId).get().getOwner();
        } else {
            return getOwner(fileDetailRepository.findById(fileId).get().getPath());
        }
    }

    public Boolean checkFileAndShare(String fileItemId, String url) {
        String path = shareLinkRepository.findById(fileItemId).get().getFileItemId();
        if (fileItemId.equals(path)) {
            return true;
        } else {
            do {
                fileItemId = fileDetailRepository.findById(fileItemId).get().getPath();
            } while ((!fileItemId.equals(path)) || (!fileItemId.equals("")) || (fileItemId != null));
            if (fileItemId.equals(path)) {
                return true;
            } else return false;
        }
    }

    public int getShareOfAcl(String url, String userName) {
        ShareLink shareLink = shareLinkRepository.findById(url).get();
        if (shareLink.getUsers().contains(userName)) {
            for (int i = 0; i < shareLink.getUsers().size(); i++) {
                if (shareLink.getUsers().get(i).equals(userName)) {
                    return shareLink.getAclist().get(i);
                }
            }
        }
        if (shareLink.getAcl().equals("public")) {
            return shareLink.getAllow();
        } else if (shareLink.getAcl().equals("users") && userName != null && (!userName.equals(""))) {
            return shareLink.getAllow();
        }
        return 0;
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


}
