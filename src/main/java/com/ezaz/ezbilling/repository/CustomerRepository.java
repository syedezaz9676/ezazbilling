package com.ezaz.ezbilling.repository;

import com.ezaz.ezbilling.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository  extends MongoRepository<Customer, String> ,CustomerRepositoryCustom {
}