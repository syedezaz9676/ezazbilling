package com.ezaz.ezbilling.repository;

import com.ezaz.ezbilling.model.*;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BillingRepositry   {

    BillingDetails getMaxBillNo(String dgst);

    List<String> findBnoByCnoAndBillingDateBetween(String cno, Date startDate, Date endDate);

    @Query(value = "{'bno': ?0}")
    List<BillAggregationResult> getGstDetails(String bno);

//    List<String> findBnosByCnoAndBillingDateBetween(String cno, Date parse, Date parse1);
   List<SoldStockSummary> getSoldStockSummary(String startDate, String endDate);

   List<SoldStockSummary> getSoldStockSummaryForHsncode(String startDate, String endDate);

    public List<CompanyBillingSummary> getCompanyBillingSummary(String startDate, String endDate);
    public List<BillingDetails> getBillingDetailsByPname(String pname);

    public int findRecordWithHighestDecimal(String prefix) ;
    public void addDgst(String dgst);
    public List<SumOfBillsAmount> getAggregatedResults(String billingDate);
    public List<SalesPerGST> getGstSales(List<String> cnos, String startDate, String endDate);
    public List<MonthlySales> getSumOfAmountAfterDiscForLastSixMonths();
    public List<MonthlySales> getSumOfAmountAfterDiscForLastSixMonthsPerCompany(String productCompany,int noOfMonths);
    public List<ProductQty> getProductSales(String productCompany, String startDate, String endDate);
    public List<ProductSixMonthsSales> getProductSalesMontly(String productCompany, String productName, int numberOfMonths);
}
