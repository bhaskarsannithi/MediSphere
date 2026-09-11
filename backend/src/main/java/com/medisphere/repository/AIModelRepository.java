package com.medisphere.repository;

import com.medisphere.model.AIModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AIModelRepository extends MongoRepository<AIModel, String> {
}
