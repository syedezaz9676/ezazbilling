package com.ezaz.ezbilling.repository.impl;

import com.ezaz.ezbilling.model.CompanyDetails;
import com.ezaz.ezbilling.model.ProductDetails;
import com.ezaz.ezbilling.model.ProductNames;
import com.ezaz.ezbilling.repository.ProductDetailsRepository;
import com.ezaz.ezbilling.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public class ProductRepositoryImpl implements ProductDetailsRepository {



    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public List<ProductNames> getProductNames(String id) {

        AggregationOperation matchOperation = Aggregation.match(
                Criteria.where("dgst").is(id).and("status").ne("D")
        );
        Aggregation aggregation = Aggregation.newAggregation(matchOperation,
                Aggregation.project("_id", "pname")
        );

        AggregationResults<ProductNames> results = mongoTemplate.aggregate(aggregation, "product", ProductNames.class);
        return results.getMappedResults();
    }

    @Override
    public List<ProductDetails> getProductDetailsByComapany(String companyName) {

        AggregationOperation matchOperation = Aggregation.match(
                Criteria.where("pcom").is(companyName).and("status").ne("D")
        );
//        Aggregation aggregation = Aggregation.newAggregation(matchOperation,
//                Aggregation.project("_id", "pname")
//        );

        AggregationResults<ProductDetails> results = mongoTemplate.aggregate(null, "product", ProductDetails.class);
        return results.getMappedResults();
    }

    public void addDgst(String dgst) {
        Query query = new Query();
        query.addCriteria(Criteria.where("dgst").exists(false));

        Update update = new Update();
        update.set("dgst", dgst);
        update.set("cess", 0);

        mongoTemplate.updateMulti(query, update, ProductDetails.class);
    }
}
