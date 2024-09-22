package com.ezaz.ezbilling.repository.impl;

import com.ezaz.ezbilling.model.BillingDetails;
import com.ezaz.ezbilling.model.CompanyDetails;
import com.ezaz.ezbilling.model.CompanyNames;
import com.ezaz.ezbilling.repository.CompanyDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CompanyDetailsRepositoryImpl  implements CompanyDetailsRepository {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public List<CompanyNames> getCompanyNames(String id) {

        AggregationOperation matchOperation = Aggregation.match(
                Criteria.where("dgst").is(id).and("status").ne("D")
        );
        Aggregation aggregation = Aggregation.newAggregation(matchOperation,
                Aggregation.project("id", "name")
        );

        AggregationResults<CompanyNames> results = mongoTemplate.aggregate(aggregation, "companysdetails", CompanyNames.class);
        return results.getMappedResults();
    }
    public void addDgst(String dgst) {
        Query query = new Query();
        query.addCriteria(Criteria.where("dgst").exists(false));

        Update update = new Update();
        update.set("dgst", dgst);

        mongoTemplate.updateMulti(query, update, CompanyDetails.class);
    }
}
