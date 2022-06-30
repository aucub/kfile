package com.example.kfile.service;

import com.example.kfile.domain.ShareLink;
import com.example.kfile.repository.FileDetailRepository;
import com.example.kfile.repository.SandBoxRepository;
import com.example.kfile.repository.ShareLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService {
    FileDetailRepository fileInfoRepository;
    SandBoxRepository sandBoxRepository;
    ShareLinkRepository shareLinkRepository;

    @Autowired
    public void setFileInfoRepository(FileDetailRepository fileInfoRepository) {
        this.fileInfoRepository = fileInfoRepository;
    }

    @Autowired
    public void setSandBoxRepository(SandBoxRepository sandBoxRepository) {
        this.sandBoxRepository = sandBoxRepository;
    }

    @Autowired
    public void setShareLinkRepository(ShareLinkRepository shareLinkRepository) {
        this.shareLinkRepository = shareLinkRepository;
    }

    public String getOwner(String fileId) {
        if (fileInfoRepository.findById(fileId).get().getPath() == null || fileInfoRepository.findById(fileId).get().getPath().equals("")) {
            return sandBoxRepository.findById(fileId).get().getOwner();
        } else {
            return getOwner(fileInfoRepository.findById(fileId).get().getPath());
        }
    }

    public Boolean checkFileAndShare(String fileItemId, String url) {
        String path = shareLinkRepository.findById(fileItemId).get().getFileItemId();
        if (fileItemId.equals(path)) {
            return true;
        } else {
            do {
                fileItemId = fileInfoRepository.findById(fileItemId).get().getPath();
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

}
