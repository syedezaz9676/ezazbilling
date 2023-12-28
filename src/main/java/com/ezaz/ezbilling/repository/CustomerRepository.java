package com.ezaz.ezbilling.repository;

import com.ezaz.ezbilling.model.Customer;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Primary
public interface CustomerRepository  extends MongoRepository<Customer, String> ,CustomerRepositoryCustom {

    List<Customer> findAllByDgst(String dgst);
}