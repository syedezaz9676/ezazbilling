package com.ezaz.ezbilling.repository;

import com.ezaz.ezbilling.model.GstCodeDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GstCodeDetailsRepo extends MongoRepository<GstCodeDetails,String> {
}
