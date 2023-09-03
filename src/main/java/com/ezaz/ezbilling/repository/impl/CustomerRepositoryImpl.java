package com.ezaz.ezbilling.repository.impl;

import com.ezaz.ezbilling.model.Customer;
import com.ezaz.ezbilling.repository.CustomerRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

public class CustomerRepositoryImpl implements CustomerRepositoryCustom {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Customer maxCustomer() {
        final Query query = new Query()
                .limit(1)
                .with(Sort.by(Sort.Direction.DESC, "cno"));

        return mongoTemplate.findOne(query, Customer.class);
    }
}