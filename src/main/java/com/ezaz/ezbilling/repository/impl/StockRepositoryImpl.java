package com.ezaz.ezbilling.repository.impl;

import com.ezaz.ezbilling.model.BillingDetails;
import com.ezaz.ezbilling.model.StockDetails;
import com.ezaz.ezbilling.repository.StockRepository;
import org.bson.Document;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class StockRepositoryImpl implements StockRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void updateStock(StockDetails stockDetails) {
        // Get the collection using mongoTemplate
        String collectionName = "in_stock_itemss"; // adjust the collection name as needed
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").is(stockDetails.get_id())),
                new Update()
                .set("in_stock_units", stockDetails.getIn_stock_units())
                .set("last_updated_date", stockDetails.getLast_updated_date()),
                collectionName);
    }

    public void updateBillItemInStock(StockDetails stockDetails) {
        // Get the collection using mongoTemplate
        System.out.println("in us"+stockDetails);
        String collectionName = "in_stock_itemss"; // adjust the collection name as needed
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").is(stockDetails.get_id())),
                new Update()
                        .set("in_stock_units", stockDetails.getIn_stock_units()),
                collectionName);
    }




}
