package com.example.kfile.repository;

import com.example.kfile.domain.FileInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FileInfoRepository extends MongoRepository<FileInfo, String> {
    List<FileInfo> findFileInfosByPath(String path);
}
