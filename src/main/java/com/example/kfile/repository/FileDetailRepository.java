package com.example.kfile.repository;

import com.example.kfile.domain.FileDetail;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileDetailRepository extends MongoRepository<FileDetail, String> {
    FileDetail findFileDetailBySha256sum(String sha256sum);
}
