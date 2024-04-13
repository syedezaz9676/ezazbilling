package com.ezaz.ezbilling.repository.impl;

import com.ezaz.ezbilling.model.*;
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
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
                .sum("Amount_after_disc").as("totalAmount")
                .first("billing_date").as("billingDate")
                .first("product_gst").as("product_gst")
                .first("bno").as("bno")
                ;




        Aggregation aggregation = Aggregation.newAggregation(match, group);

        AggregationResults<BillAggregationResult> result = mongoTemplate.aggregate(aggregation, "soldstock", BillAggregationResult.class);
        gstDetailsList = result.getMappedResults();

        return gstDetailsList;
    }

//    public List<SoldStockSummary> getSoldStockSummary(String startDate, String endDate) {
//        Aggregation aggregation = Aggregation.newAggregation(
//                Aggregation.match(Criteria.where("billing_date").gte(startDate).lte(endDate)),
//                Aggregation.lookup("customers", "cno", "cno", "customers"),
//                Aggregation.match(Criteria.where("customers.ctno").ne("not available").and("customers.isigst").is("No")),
//                Aggregation.project("hsn_code", "Amount_after_disc", "qty","product_gst"), // Include hsn_code in projection
//                Aggregation.group("hsn_code")
//                        .addToSet("hsn_code").as("hsn_code")
//                        .first("product_gst").as("product_gst")
//                        .sum("Amount_after_disc").as("totalAmount")
//                        .sum("qty").as("totalQty")
//
//        );
//
//        AggregationResults<SoldStockSummary> results = mongoTemplate.aggregate(aggregation, "soldstock", SoldStockSummary.class);
//        return results.getMappedResults();
//    }

    public List<SoldStockSummary> getSoldStockSummaryForHsncode(String startDate, String endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("billing_date").gte(startDate).lte(endDate)), // Filtering based on billing date
                Aggregation.lookup("customers", "cno", "cno", "customers"), // Perform a left outer join with the customers collection
                Aggregation.match(Criteria.where("customers.ctno").ne("not available").and("customers.isigst").is("Yes")), // Filter customers based on certain conditions
                Aggregation.project("hsn_code", "Amount_after_disc", "qty", "product_gst"), // Include necessary fields in the projection stage
                Aggregation.group("hsn_code")
                        .addToSet("hsn_code").as("hsn_code") // Group by HSN code
                        .first("product_gst").as("product_gst") // Take the first product GST value encountered
                        .sum("Amount_after_disc").as("totalAmount") // Sum the total amount after discount
                        .sum("qty").as("totalQty"), // Sum the total quantity
                Aggregation.project("hsn_code", "totalAmount", "totalQty", "product_gst"), // Reproject fields
                Aggregation.project()
                        .andExpression("totalAmount * product_gst / 100").as("igst") // Calculate the tax amount
                        .and("hsn_code").as("hsn_code")
                        .and("totalAmount").as("totalAmount")
                        .and("totalQty").as("totalQty")
                        .and("product_gst").as("product_gst")
        );

        AggregationResults<SoldStockSummary> results = mongoTemplate.aggregate(aggregation, "soldstock", SoldStockSummary.class); // Execute aggregation pipeline
        return results.getMappedResults(); // Return the mapped results
    }

    public List<SoldStockSummary> getSoldStockSummary(String startDate, String endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("billing_date").gte(startDate).lte(endDate)), // Filtering based on billing date
                Aggregation.lookup("customers", "cno", "cno", "customers"), // Perform a left outer join with the customers collection
                Aggregation.match(Criteria.where("customers.ctno").ne("not available").and("customers.isigst").is("No")), // Filter customers based on certain conditions
                Aggregation.project("hsn_code", "Amount_after_disc", "qty", "product_gst"), // Include necessary fields in the projection stage
                Aggregation.group("hsn_code")
                        .addToSet("hsn_code").as("hsn_code") // Group by HSN code
                        .first("product_gst").as("product_gst") // Take the first product GST value encountered
                        .sum("Amount_after_disc").as("totalAmount") // Sum the total amount after discount
                        .sum("qty").as("totalQty"), // Sum the total quantity
                Aggregation.project("hsn_code", "totalAmount", "totalQty", "product_gst"), // Reproject fields
                Aggregation.project()
                        .andExpression("totalAmount * product_gst / 100").as("taxAmount") // Calculate the tax amount
                        .and("hsn_code").as("hsn_code")
                        .and("totalAmount").as("totalAmount")
                        .and("totalQty").as("totalQty")
                        .and("product_gst").as("product_gst")
        );

        AggregationResults<SoldStockSummary> results = mongoTemplate.aggregate(aggregation, "soldstock", SoldStockSummary.class); // Execute aggregation pipeline
        return results.getMappedResults(); // Return the mapped results
    }




}

