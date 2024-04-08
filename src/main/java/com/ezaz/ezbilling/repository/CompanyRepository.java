package com.ezaz.ezbilling.repository;

import com.ezaz.ezbilling.model.CompanyDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends MongoRepository<CompanyDetails, String> {

    List<CompanyDetails> findAllByDgst(String id);


}
