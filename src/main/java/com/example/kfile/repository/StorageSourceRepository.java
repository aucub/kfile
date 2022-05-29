package com.example.kfile.repository;

import com.example.kfile.todo.StorageSource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface StorageSourceRepository extends MongoRepository<StorageSource, String> {
    @Query("{'enableStorage': true}")
    List<StorageSource> findByEnableStorageTrue();
}
