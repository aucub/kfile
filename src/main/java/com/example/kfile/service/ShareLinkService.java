package com.example.kfile.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.example.kfile.domain.ShareLink;
import com.example.kfile.repository.ShareLinkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 文件分享 Service
 */
@Service
@Slf4j
@CacheConfig(cacheNames = "shareLink")
public class ShareLinkService {

    private ShareLinkRepository shareLinkRepository;

    @Autowired
    public void setShareLinkRepository(ShareLinkRepository shareLinkRepository) {
        this.shareLinkRepository = shareLinkRepository;
    }

    @Cacheable(key = "#url", unless = "#result == null")
    public ShareLink findById(String url) {
        return shareLinkRepository.findById(url).get();
    }


    /**
     * 生成分享
     */
    public ShareLink generatorShareLink(String fileId, Long expireTime) {
        boolean validate = checkExpireDateIsValidate(expireTime);
        if (!validate) {
            throw new IllegalArgumentException("过期时间不合法");
        }
        ShareLink shareLink;
        String randomKey;
        int generateCount = 0;
        do {
            randomKey = RandomUtil.randomString(6);
            shareLink = findById(randomKey);
            generateCount++;
        } while (shareLink != null);

        shareLink = new ShareLink();
        shareLink.setUrl(randomKey);
        shareLink.setCreateDate(new Date());
        shareLink.setFileId(fileId);

        if (expireTime == -1) {
            shareLink.setExpireDate(DateUtil.parseDate("9999-12-31"));
        } else {
            shareLink.setExpireDate(new Date(System.currentTimeMillis() + expireTime));
        }

        shareLinkRepository.save(shareLink);
        return shareLink;
    }


    //删除分享
    @CacheEvict(allEntries = true)
    public void removeById(String url) {
        shareLinkRepository.deleteById(url);
    }


    // 检查分享时间是否有效
    private boolean checkExpireDateIsValidate(Long expires) {
        return expires > 0 || expires == -1;
    }

}