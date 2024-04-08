package com.ezaz.ezbilling.repository.impl;

import com.ezaz.ezbilling.model.BillAggregationResult;
import com.ezaz.ezbilling.model.BillDetails;
import com.ezaz.ezbilling.model.BillingDetails;
import com.ezaz.ezbilling.model.Customer;
import com.ezaz.ezbilling.repository.BillingRepositry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
public class BillingRepositryImpl  implements BillingRepositry {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public BillingDetails getMaxBillNo(String dgst) {
        final Query query = new Query()
                .limit(1)
                .with(Sort.by(Sort.Direction.DESC, "bno"));
        query.addCriteria(Criteria.where("dgst").is(dgst));
        return mongoTemplate.findOne(query, BillingDetails.class);
    }


    @Override
//    public List<String> findBnoByCnoAndBillingDateBetween(String cno, Date startDate, Date endDate) {
//        System.out.println("startdate"+startDate);
//        Aggregation aggregation = Aggregation.newAggregation(
//                Aggregation.match(Criteria.where("cno").is(cno)
//                        .and("billing_date").gte("2017-01-01").lte("2023-01-01")),
//                Aggregation.group("bno")
//        );
//
//        AggregationResults<String> results = mongoTemplate.aggregate(aggregation, "soldstock", String.class);
//        System.out.println("resulst"+results.getMappedResults());
//        return results.getMappedResults();
//    }

    public List<String> findBnoByCnoAndBillingDateBetween(String cno, Date startDate, Date endDate) {
        Query query = new Query(Criteria.where("cno").is(cno)
                .and("billing_date").gte(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).lte(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));

        List<String> bnos = mongoTemplate.findDistinct(query, "bno", BillingDetails.class, String.class);
        System.out.println("bnos"+bnos);
        return bnos;
    }



    @Override
    public List<BillAggregationResult> getGstDetails(String bno) {
        List<BillAggregationResult> gstDetailsList = new ArrayList<>();

        AggregationOperation match = Aggregation.match(org.springframework.data.mongodb.core.query.Criteria.where("bno").is(bno));
        AggregationOperation group = Aggregation.group("product_gst", "bno" ,"billing_date")
                .sum("amount").as("totalAmount")
                .first("billing_date").as("billingDate")
                .first("product_gst").as("product_gst")
                .first("bno").as("bno")
                ;




        Aggregation aggregation = Aggregation.newAggregation(match, group);

        AggregationResults<BillAggregationResult> result = mongoTemplate.aggregate(aggregation, "soldstock", BillAggregationResult.class);
        gstDetailsList = result.getMappedResults();

        return gstDetailsList;
    }

}

