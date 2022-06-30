package com.example.kfile.repository;

import com.example.kfile.domain.FileItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FileItemRepository extends MongoRepository<FileItem, String> {
    List<FileItem> findFileItemByDirectory(String directory);
}
