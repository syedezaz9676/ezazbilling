package com.ezaz.ezbilling.repository.impl;

import com.ezaz.ezbilling.model.CompanyNames;
import com.ezaz.ezbilling.model.Customer;
import com.ezaz.ezbilling.model.CustomerDetailswithGstNo;
import com.ezaz.ezbilling.model.CustomerNames;
import com.ezaz.ezbilling.repository.CustomerRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

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

    @Override
    public List<CustomerNames> getCustomerNames(String id) {
        AggregationOperation matchOperation = Aggregation.match(
                Criteria.where("dgst").is(id)
        );
        Aggregation aggregation = Aggregation.newAggregation(matchOperation,
                Aggregation.project("id", "cname")
        );

        AggregationResults<CustomerNames> results = mongoTemplate.aggregate(aggregation, "customers", CustomerNames.class);
        return results.getMappedResults();
    }

    @Override
    public List<CustomerDetailswithGstNo> findGstCustomers() {
        Query query = new Query(Criteria.where("ctno").ne("not avaliable"));
        return mongoTemplate.find(query, CustomerDetailswithGstNo.class, "customers");
    }



}