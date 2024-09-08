package com.ezaz.ezbilling.repository.impl;

import com.ezaz.ezbilling.model.*;
import com.ezaz.ezbilling.repository.CustomerRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.stream.Collectors;

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
//        Query query = new Query(Criteria.where("ctno").ne("not avaliable"));
        Query query = new Query(Criteria.where("ctno").not().regex("^not"));
        return mongoTemplate.find(query, CustomerDetailswithGstNo.class, "customers");
    }

    public List<String> findGstCustomerIdsWithoutIgst() {
        Query query = new Query();
//        query.addCriteria(Criteria.where("ctno").ne("not avaliable").and("isigst").is("No"));
        query.addCriteria(Criteria.where("ctno").not().regex("^not").and("isigst").is("No"));
        List<Customer> customers = mongoTemplate.find(query, Customer.class);
        return customers.stream().map(Customer::getId).collect(Collectors.toList());
    }
    public List<String> findGstCustomerIdsWithIgst() {
        Query query = new Query();
//        query.addCriteria(Criteria.where("ctno").ne("not avaliable").and("isigst").is("Yes"));
        query.addCriteria(Criteria.where("ctno").not().regex("^not").and("isigst").is("Yes"));
        List<Customer> customers = mongoTemplate.find(query, Customer.class);
        return customers.stream().map(Customer::getId).collect(Collectors.toList());
    }

    public List<String> findGstCustomerIdsWithGst() {
        Query query = new Query();
//        query.addCriteria(Criteria.where("ctno").ne("not avaliable"));
        query.addCriteria(Criteria.where("ctno").not().regex("^not"));
        List<Customer> customers = mongoTemplate.find(query, Customer.class);
        return customers.stream().map(Customer::getId).collect(Collectors.toList());
    }
    public List<String> findCustomerIds() {
        Query query = new Query();
//        query.addCriteria(Criteria.where("ctno").ne("not available").and("isigst").is("Yes"));
        List<Customer> customers = mongoTemplate.find(query, Customer.class);
        return customers.stream().map(Customer::getId).collect(Collectors.toList());
    }



    public void addDgst(String dgst) {
        Query query = new Query();
        query.addCriteria(Criteria.where("dgst").exists(false));

        Update update = new Update();
        update.set("dgst", dgst);

        mongoTemplate.updateMulti(query, update, Customer.class);
    }


}