package com.example.kfile.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.example.kfile.domain.ShareLink;
import com.example.kfile.exception.InvalidShareLinkException;
import com.example.kfile.repository.FileItemRepository;
import com.example.kfile.repository.ShareLinkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

/**
 * 文件分享 Service
 */
@Service
@Slf4j
public class ShareLinkService {

    private ShareLinkRepository shareLinkRepository;

    private FileItemRepository fileItemRepository;

    @Autowired
    public void setShareLinkRepository(ShareLinkRepository shareLinkRepository) {
        this.shareLinkRepository = shareLinkRepository;
    }

    @Autowired
    public void setFileItemRepository(FileItemRepository fileItemRepository) {
        this.fileItemRepository = fileItemRepository;
    }

    public ShareLink findById(String url) {
        return shareLinkRepository.findById(url).get();
    }

    /**
     * 生成分享链接
     *
     * @param fileItemId 文件项ID
     * @param expireTime 过期时间
     * @return 分享链接
     * @throws FileNotFoundException 文件未找到异常
     */
    public ShareLink generatorShareLink(String fileItemId, Long expireTime) throws FileNotFoundException {
        // 检查过期时间是否合法
        boolean validate = checkExpireDateIsValidate(expireTime);
        if (!validate) {
            throw new IllegalArgumentException("过期时间不合法");
        }
        ShareLink shareLink;
        String randomKey;
        do {
            // 生成随机字符串作为链接
            randomKey = RandomUtil.randomString(6);
            // 根据随机字符串查找分享链接
            shareLink = findById(randomKey);
            if (shareLink != null) {
                // 如果分享链接存在且不合法，则跳出循环
                if (!checkShareIsValidate(shareLink.getUrl())) {
                    break;
                }
            }
        } while (shareLink != null);
        // 创建新的分享链接
        shareLink = new ShareLink();
        shareLink.setUrl(randomKey);
        shareLink.setCreateDate(new Date());
        shareLink.setFileItemId(fileItemId);
        // 设置过期时间
        if (expireTime == -1) {
            shareLink.setExpireDate(DateUtil.parseDate("9999-12-31"));
        } else {
            shareLink.setExpireDate(new Date(System.currentTimeMillis() + expireTime));
        }
        // 保存分享链接
        shareLinkRepository.save(shareLink);
        return shareLink;
    }


    //删除分享
    public void delete(String url) {
        shareLinkRepository.deleteById(url);
    }


    // 检查设置分享到期时间是否有效
    private boolean checkExpireDateIsValidate(Long expires) {
        return expires > 0 || expires == -1;
    }

    // 检查分享是否有效
    private boolean checkShareIsValidate(String url) throws FileNotFoundException {
        // 根据URL查找分享链接
        ShareLink shareLink = shareLinkRepository.findById(url).get();
        // 如果找到了分享链接
        if (shareLink != null) {
            // 如果分享链接的过期日期在当前日期之后
            if (shareLink.getExpireDate().after(new Date())) {
                return true;
            } else {
                return false;
            }
        }
        // 如果分享链接不存在，则抛出文件未找到异常
        throw new FileNotFoundException("分享不存在");
    }

    /**
     * 检查文件是否对应url
     *
     * @param fileItemId
     * @param url
     * @return
     */
    public Boolean checkFileAndShare(String fileItemId, String url) {
        // 获取文件项的路径
        String path = shareLinkRepository.findById(fileItemId).get().getFileItemId();
        // 检查文件项的路径是否与给定的fileItemId相等
        if (fileItemId.equals(path)) {
            return true;
        } else {
            // 循环查找文件项的目录，直到找到与路径相等的目录或遇到空目录或null
            do {
                fileItemId = fileItemRepository.findById(fileItemId).get().getDirectory();
            } while ((!fileItemId.equals(path)) || (!fileItemId.equals("")) || (fileItemId != null));
            // 检查最终找到的目录是否与路径相等
            if (fileItemId.equals(path)) {
                return true;
            } else {
                return false;
            }
        }
    }


    /**
     * 获取分享文件的权限
     *
     * @param url
     * @param userName
     * @return
     */
    public int getShareOfAcl(String url, String userName) {
        ShareLink shareLink = shareLinkRepository.findById(url).get();
        if (shareLink == null) {
            throw new InvalidShareLinkException("分享不存在");
        }
        List<String> users = shareLink.getUsers();
        List<Integer> aclist = shareLink.getAclist();
        if (users.contains(userName)) {
            int index = users.indexOf(userName);
            return aclist.get(index);
        }
        String acl = shareLink.getAcl();
        if (acl.equals("public")) {
            return shareLink.getAllow();
        } else if (acl.equals("users") && userName != null && (!userName.equals(""))) {
            return shareLink.getAllow();
        }
        return 0;
    }

}