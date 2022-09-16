package com.example.kfile.repository;

import com.example.kfile.domain.FileDetail;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FileDetailRepository extends MongoRepository<FileDetail, String> {
    List<FileDetail> findFileDetailBySha256sum(String sha256sum);
}
