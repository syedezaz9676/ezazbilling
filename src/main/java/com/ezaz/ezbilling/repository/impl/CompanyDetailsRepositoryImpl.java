package com.ezaz.ezbilling.repository.impl;

import com.ezaz.ezbilling.model.CompanyNames;
import com.ezaz.ezbilling.repository.CompanyDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CompanyDetailsRepositoryImpl  implements CompanyDetailsRepository {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public List<CompanyNames> getCompanyNames(String id) {

        AggregationOperation matchOperation = Aggregation.match(
                Criteria.where("dgst").is(id)
        );
        Aggregation aggregation = Aggregation.newAggregation(matchOperation,
                Aggregation.project("id", "name")
        );

        AggregationResults<CompanyNames> results = mongoTemplate.aggregate(aggregation, "companysdetails", CompanyNames.class);
        return results.getMappedResults();
    }
}
