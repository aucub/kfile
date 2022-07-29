package com.example.kfile.repository;

import com.example.kfile.domain.ShareLink;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 文件分享表
 */
public interface ShareLinkRepository extends MongoRepository<ShareLink, String> {
}