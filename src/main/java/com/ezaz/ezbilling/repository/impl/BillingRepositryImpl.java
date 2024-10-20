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

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
        query.addCriteria(where("dgst").is(dgst));
        return mongoTemplate.findOne(query, BillingDetails.class);
    }


    @Override

    public List<String> findBnoByCnoAndBillingDateBetween(String cno, Date startDate, Date endDate) {
        Query query = new Query(where("cno").is(cno)
                .and("billing_date").gte(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).lte(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));

        List<String> bnos = mongoTemplate.findDistinct(query, "bno", BillingDetails.class, String.class);
        return bnos;
    }



    @Override
    public List<BillAggregationResult> getGstDetails(String bno) {
        List<BillAggregationResult> gstDetailsList = new ArrayList<>();

        AggregationOperation match = Aggregation.match(where("bno").is(bno));
        AggregationOperation group = Aggregation.group("product_gst", "bno" ,"billing_date")
                .sum("amount_after_disc").as("totalAmount")
                .first("cessAmount").as("totalCessAmount")
                .first("billing_date").as("billingDate")
                .first("product_gst").as("product_gst")
                .first("bno").as("bno")
                .sum("netAmount").as("netAmount")
                .first("cess").as("cess")
                ;




        Aggregation aggregation = Aggregation.newAggregation(match, group);

        AggregationResults<BillAggregationResult> result = mongoTemplate.aggregate(aggregation, "soldstock", BillAggregationResult.class);
        gstDetailsList = result.getMappedResults();

        return gstDetailsList;
    }


    public List<SoldStockSummary> getSoldStockSummaryForHsncode(String startDate, String endDate) {

        List<String> customerNumbers = customerRepository.findGstCustomerIdsWithoutIgst();
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(where("billing_date").gte(startDate).lte(endDate)), // Filtering based on billing date
                Aggregation.match(where("cno").in(customerNumbers)), // Filter based on cnoList
                Aggregation.group("hsn_code")
                        .addToSet("hsn_code").as("hsn_code") // Group by HSN code
                        .first("product_gst").as("product_gst") // Take the first product GST value encountered
                        .sum("amount_after_disc").as("totalAmount")
                        .sum("netAmount").as("totalNetAmount")
                        .sum("cessAmount").as("totalCessAmount")/// Sum the total amount after discount
                        .sum("qty").as("totalQty"), // Sum the total quantity
                Aggregation.project("hsn_code", "totalAmount", "totalQty", "product_gst","totalCessAmount","totalNetAmount"), // Reproject fields
                Aggregation.project()
                         // Calculate the tax amount
                        .andExpression("totalNetAmount").as("taxableAmount")
                        .andExpression("(totalNetAmount/100)*product_gst").as("taxAmount")
                        .and("hsn_code").as("hsn_code")
                        .and("totalAmount").as("totalAmount")
                        .and("totalQty").as("totalQty")
                        .and("product_gst").as("product_gst")
                        .and("totalCessAmount").as("totalCessAmount")
        );

        AggregationResults<SoldStockSummary> results = mongoTemplate.aggregate(aggregation, "soldstock", SoldStockSummary.class); // Execute aggregation pipeline
        return results.getMappedResults();
    }

    public List<SoldStockSummary> getSoldStockSummary(String startDate, String endDate) {
        List<String> customerNumbers = customerRepository.findGstCustomerIdsWithIgst();
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(where("billing_date").gte(startDate).lte(endDate)), // Filtering based on billing date
                Aggregation.match(where("cno").in(customerNumbers)), // Filter based on cnoList
                Aggregation.group("hsn_code")
                        .addToSet("hsn_code").as("hsn_code") // Group by HSN code
                        .first("product_gst").as("product_gst") // Take the first product GST value encountered
                        .sum("amount_after_disc").as("totalAmount")
                        .sum("netAmount").as("totalNetAmount")
                        .sum("cessAmount").as("totalCessAmount")// Sum the total amount after discount
                        .sum("qty").as("totalQty"), // Sum the total quantity
                Aggregation.project("hsn_code", "totalAmount", "totalQty", "product_gst","totalNetAmount","totalCessAmount"), // Reproject fields
                Aggregation.project()
                        .andExpression("(totalNetAmount/100)*product_gst").as("igst") // Calculate the tax amount
                        .andExpression("totalNetAmount").as("taxableAmount")
                        .and("hsn_code").as("hsn_code")
                        .and("totalAmount").as("totalAmount")
                        .and("totalQty").as("totalQty")
                        .and("product_gst").as("product_gst")
                        .and("totalCessAmount").as("totalCessAmount")
        );

        AggregationResults<SoldStockSummary> results = mongoTemplate.aggregate(aggregation, "soldstock", SoldStockSummary.class); // Execute aggregation pipeline
        return results.getMappedResults(); // Return the mapped results
    }

    public List<CompanyBillingSummary> getCompanyBillingSummary(String startDate, String endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(where("billing_date").gte(startDate).lte(endDate)),
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

        AggregationOperation match = Aggregation.match(where("product_name").is(pname));





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
        query.addCriteria(where("dgst").exists(false));

        Update update = new Update();
        update.set("dgst", dgst);
//        update.rename("Amount_after_disc", "amount_after_disc");

        mongoTemplate.updateMulti(query, update, BillingDetails.class);
    }

    public List<SumOfBillsAmount> getAggregatedResults(String billingDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(where("billing_date").is(billingDate)),
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
                Aggregation.match(where("cno").in(cnos)
                        .andOperator(where("billing_date").gte(startDate),
                                where("billing_date").lte(endDate))),
                Aggregation.group("product_gst")
                        .sum("amount_after_disc").as("totalAmountAfterDisc")
                        .first("product_gst").as("productGst")

        );

        AggregationResults<SalesPerGST> results = mongoTemplate.aggregate(aggregation, "soldstock", SalesPerGST.class);
        return results.getMappedResults();
    }

    public List<MonthlySales> getSumOfAmountAfterDiscForLastSixMonths() {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(13);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Aggregation aggregation = Aggregation.newAggregation(
                // Match billing_date within the last 6 months
                Aggregation.match(Criteria.where("billing_date").gte(sixMonthsAgo.format(formatter))),

                // Extract the year and month (yyyy-mm) from billingDate
                Aggregation.project()
                        .andExpression("substr(billing_date, 0, 7)").as("monthYear")
                        .and("amount_after_disc").as("amountAfterDisc")
                ,

                // Group by the extracted monthYear and sum the amounts
                Aggregation.group("monthYear")
                        .sum("amountAfterDisc").as("totalAmount")


        );

        // Execute the aggregation query on the soldstock collection
        AggregationResults<MonthlySales> results = mongoTemplate.aggregate(aggregation, "soldstock", MonthlySales.class);

        return results.getMappedResults();
    }

    public List<MonthlySales> getSumOfAmountAfterDiscForLastSixMonthsPerCompany(String productCompany,int noOfMonths) {
        // Get the date from six months ago
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(noOfMonths);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Define the aggregation query
        Aggregation aggregation = Aggregation.newAggregation(
                // Match billing_date within the last 6 months and filter by product_company
                Aggregation.match(Criteria.where("billing_date").gte(sixMonthsAgo.format(formatter))
                        .and("product_company").is(productCompany)),

                // Extract the year and month (yyyy-MM) from billing_date
                Aggregation.project()
                        .andExpression("substr(billing_date, 0, 7)").as("monthYear")
                        .and("amount_after_disc").as("amountAfterDisc"),

                // Group by monthYear and sum the amount_after_disc
                Aggregation.group("monthYear")
                        .sum("amountAfterDisc").as("totalAmount")
        );

        // Execute the aggregation query on the soldstock collection
        AggregationResults<MonthlySales> results = mongoTemplate.aggregate(aggregation, "soldstock", MonthlySales.class);

        return results.getMappedResults();
    }

    public List<ProductQty> getProductSales(String productCompany, String startDate, String endDate) {

        // Match documents with billing_date in the given range
        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("billing_date").gte(startDate).lte(endDate).and("product_company").is(productCompany)
        );

        // Group by product_company and sum qty
        GroupOperation groupOperation = Aggregation.group("product_name")
                .sum("qty").as("totalQty")
                .sum("amount_after_disc").as("totalAmount")
                .first("product_name").as("productName");

        // Build the aggregation pipeline
        Aggregation aggregation = Aggregation.newAggregation(matchOperation, groupOperation);

        // Execute the aggregation
        AggregationResults<ProductQty> result = mongoTemplate.aggregate(
                aggregation, "soldstock", ProductQty.class
        );
        System.out.println("result"+result.getMappedResults());
        return result.getMappedResults();
    }
    public List<ProductSixMonthsSales> getProductSalesMontly(String productCompany, String productName, int numberOfMonths) {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(numberOfMonths);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Define the aggregation query
        Aggregation aggregation = Aggregation.newAggregation(
                // Match billing_date within the last 6 months and filter by product_company
                Aggregation.match(Criteria.where("billing_date").gte(sixMonthsAgo.format(formatter))
                        .and("product_company").is(productCompany).and("product_name").is(productName))
                ,

                // Extract the year and month (yyyy-MM) from billing_date
                Aggregation.project()
                        .andExpression("substr(billing_date, 0, 7)").as("monthYear")
                        .and("amount_after_disc").as("amountAfterDisc")
                        .and("qty").as("qty"),

                // Group by monthYear and sum the amount_after_disc
                Aggregation.group("monthYear")
                        .sum("amountAfterDisc").as("totalAmount")
                .sum("qty").as("totalQty"),
                Aggregation.sort(Sort.Direction.ASC, "monthYear")
        );

        AggregationResults<ProductSixMonthsSales> aggregationResults = mongoTemplate.aggregate(aggregation, "soldstock", ProductSixMonthsSales.class);
        return aggregationResults.getMappedResults();
    }



}

