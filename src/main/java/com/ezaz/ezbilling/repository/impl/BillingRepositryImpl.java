package com.ezaz.ezbilling.repository.impl;

import com.ezaz.ezbilling.model.*;
import com.ezaz.ezbilling.repository.BillItemsRepository;
import com.ezaz.ezbilling.repository.BillingRepositry;
import com.ezaz.ezbilling.repository.CustomerRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class BillingRepositryImpl  implements BillingRepositry {

    @Autowired
    private MongoTemplate mongoTemplate;

    private final CustomerRepositoryCustom customerRepository;
    private final BillItemsRepository billItemsRepository;

    public BillingRepositryImpl(CustomerRepositoryCustom customerRepository, BillItemsRepository billItemsRepository) {
        this.customerRepository = customerRepository;
        this.billItemsRepository = billItemsRepository;
    }

    @Override
    public BillingDetails getMaxBillNo(String dgst) {
        final Query query = new Query()
                .limit(1)
                .with(Sort.by(Sort.Direction.DESC, "bno"));
        query.addCriteria(Criteria.where("dgst").is(dgst));
        return mongoTemplate.findOne(query, BillingDetails.class);
    }


    @Override

    public List<String> findBnoByCnoAndBillingDateBetween(String cno, Date startDate, Date endDate) {
        Query query = new Query(Criteria.where("cno").is(cno)
                .and("billing_date").gte(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).lte(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));

        List<String> bnos = mongoTemplate.findDistinct(query, "bno", BillingDetails.class, String.class);
        return bnos;
    }



    @Override
    public List<BillAggregationResult> getGstDetails(String bno) {
        List<BillAggregationResult> gstDetailsList = new ArrayList<>();

        AggregationOperation match = Aggregation.match(org.springframework.data.mongodb.core.query.Criteria.where("bno").is(bno));
        AggregationOperation group = Aggregation.group("product_gst", "bno" ,"billing_date")
                .sum("amount_after_disc").as("totalAmount")
                .first("billing_date").as("billingDate")
                .first("product_gst").as("product_gst")
                .first("bno").as("bno")
                ;




        Aggregation aggregation = Aggregation.newAggregation(match, group);

        AggregationResults<BillAggregationResult> result = mongoTemplate.aggregate(aggregation, "soldstock", BillAggregationResult.class);
        gstDetailsList = result.getMappedResults();

        return gstDetailsList;
    }


    public List<SoldStockSummary> getSoldStockSummaryForHsncode(String startDate, String endDate) {

        List<String> customerNumbers = customerRepository.findGstCustomerIdsWithoutIgst();
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("billing_date").gte(startDate).lte(endDate)), // Filtering based on billing date
                Aggregation.match(Criteria.where("cno").in(customerNumbers)), // Filter based on cnoList
                Aggregation.group("hsn_code")
                        .addToSet("hsn_code").as("hsn_code") // Group by HSN code
                        .first("product_gst").as("product_gst") // Take the first product GST value encountered
                        .sum("amount_after_disc").as("totalAmount") // Sum the total amount after discount
                        .sum("qty").as("totalQty"), // Sum the total quantity
                Aggregation.project("hsn_code", "totalAmount", "totalQty", "product_gst"), // Reproject fields
                Aggregation.project()
                        .andExpression("totalAmount * product_gst / 100").as("taxAmount") // Calculate the tax amount
                        .andExpression("totalAmount-(totalAmount * product_gst / 100)").as("taxableAmount")
                        .and("hsn_code").as("hsn_code")
                        .and("totalAmount").as("totalAmount")
                        .and("totalQty").as("totalQty")
                        .and("product_gst").as("product_gst")
        );

        AggregationResults<SoldStockSummary> results = mongoTemplate.aggregate(aggregation, "soldstock", SoldStockSummary.class); // Execute aggregation pipeline
        return results.getMappedResults();
    }

    public List<SoldStockSummary> getSoldStockSummary(String startDate, String endDate) {
        List<String> customerNumbers = customerRepository.findGstCustomerIdsWithIgst();
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("billing_date").gte(startDate).lte(endDate)), // Filtering based on billing date
                Aggregation.match(Criteria.where("cno").in(customerNumbers)), // Filter based on cnoList
                Aggregation.group("hsn_code")
                        .addToSet("hsn_code").as("hsn_code") // Group by HSN code
                        .first("product_gst").as("product_gst") // Take the first product GST value encountered
                        .sum("amount_after_disc").as("totalAmount") // Sum the total amount after discount
                        .sum("qty").as("totalQty"), // Sum the total quantity
                Aggregation.project("hsn_code", "totalAmount", "totalQty", "product_gst"), // Reproject fields
                Aggregation.project()
                        .andExpression("totalAmount * product_gst / 100").as("igst") // Calculate the tax amount
                        .andExpression("totalAmount-(totalAmount * product_gst / 100)").as("taxableAmount")
                        .and("hsn_code").as("hsn_code")
                        .and("totalAmount").as("totalAmount")
                        .and("totalQty").as("totalQty")
                        .and("product_gst").as("product_gst")
        );

        AggregationResults<SoldStockSummary> results = mongoTemplate.aggregate(aggregation, "soldstock", SoldStockSummary.class); // Execute aggregation pipeline
        return results.getMappedResults(); // Return the mapped results
    }

    public List<CompanyBillingSummary> getCompanyBillingSummary(String startDate, String endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("billing_date").gte(startDate).lte(endDate)),
                Aggregation.group("product_company")
                        .sum("amount_after_disc").as("totalAmount")
                        .first("product_company").as("product_company")
        );

        AggregationResults<CompanyBillingSummary> results = mongoTemplate.aggregate(
                aggregation, "soldstock", CompanyBillingSummary.class);

        return results.getMappedResults();

    }

    public List<BillingDetails> getBillingDetailsByPname(String pname){
        List<BillingDetails>  billingDetailsList = new ArrayList<>();

        AggregationOperation match = Aggregation.match(org.springframework.data.mongodb.core.query.Criteria.where("product_name").is(pname));





        Aggregation aggregation = Aggregation.newAggregation(match);

        AggregationResults<BillingDetails> result = mongoTemplate.aggregate(aggregation, "soldstock", BillingDetails.class);
        billingDetailsList = result.getMappedResults();

        return billingDetailsList;
    }

    public int findRecordWithHighestDecimal(String prefix) {
        System.out.println("prefix"+prefix);
        List<BillingDetails> records = billItemsRepository.findByBnoStartingWith(prefix);


        int maxDecimal = 0;

        for (BillingDetails record : records) {
            int decimalPart = Integer.parseInt(record.getBno().substring(4)); // Extract decimal part after "RE"
            if (decimalPart > maxDecimal) {
                maxDecimal = decimalPart;
            }
        }

        return maxDecimal;
    }


    public void addDgst(String dgst) {
        Query query = new Query();
        query.addCriteria(Criteria.where("dgst").exists(false));

        Update update = new Update();
        update.set("dgst", dgst);
//        update.rename("Amount_after_disc", "amount_after_disc");

        mongoTemplate.updateMulti(query, update, BillingDetails.class);
    }

    public List<SumOfBillsAmount> getAggregatedResults(String billingDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("billing_date").is(billingDate)),
                Aggregation.group("bno", "billing_date")
                        .sum("amount_after_disc").as("amount")
                        .first("bno").as("bno")
                        .first("billing_date").as("date")
                        .first("cno").as("cno")
                        .first("dgst").as("dgst"),
                Aggregation.project("bno", "date", "amount","cno","dgst")
        );

        AggregationResults<SumOfBillsAmount> results = mongoTemplate.aggregate(aggregation, "soldstock", SumOfBillsAmount.class);
        return results.getMappedResults();
    }

    public List<SalesPerGST> getGstSales(List<String> cnos, String startDate, String endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("cno").in(cnos)
                        .andOperator(Criteria.where("billing_date").gte(startDate),
                                Criteria.where("billing_date").lte(endDate))),
                Aggregation.group("product_gst")
                        .sum("amount_after_disc").as("totalAmountAfterDisc")
                        .first("product_gst").as("productGst")
        );

        AggregationResults<SalesPerGST> results = mongoTemplate.aggregate(aggregation, "soldstock", SalesPerGST.class);
        return results.getMappedResults();
    }
}

