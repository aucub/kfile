package com.example.kfile.repository;

import com.example.kfile.domain.SandBox;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SandBoxRepository extends MongoRepository<SandBox, String> {
}
