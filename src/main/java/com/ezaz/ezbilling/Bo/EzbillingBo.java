package com.ezaz.ezbilling.Bo;

import com.ezaz.ezbilling.model.*;
import com.ezaz.ezbilling.model.mysql.JpaCustomer;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;


public interface EzbillingBo {

    public void saveCustomerToDB(Customer customer);

    public List<GstCodeDetails> getGstCodeDetails();

    public void saveCompanyDetails(CompanyDetails companyDetails);

    public List<CompanyDetails> getCompanyDetails(String id);

    public void saveProductDetails(ProductDetails productDetails);

    public List<ProductDetails> getProductsDetails(String id);

    public List<ProductNames> getProductNames(String id);

    public Optional<ProductDetails> getProductDetailsByID(String id);

    public List<CompanyNames> getCompanyNames(String id);
    public Optional<CompanyDetails> getCompanyDetailsByID(String id);

    Optional<Customer> getCustomerDetailsByID(String id);

    public List<CustomerNames> getCustomerNames(String id);

    public List<ProductNames> getProductDetailsByCompany(String comapanyName);

    public List<Customer> getCustomerByDgst(String dgst);

    public String saveBillItems(List<BillingDetails> billingDetailsList) throws ParseException;

    public SavedBillandWayBillDetails getSavedBillDetailsByinvoiceNo(String invoiceNo);

    public BillDetails getBillDetailsByInvoiceNo(String invoiceNo);

    public String updateBillItems(List<BillingDetails> billingDetailsList);

    public List<GstReport>  getGstDetailsByDate(String startDate, String endDate) throws ParseException;

    public List<SoldStockSummary> getGstDetailsForHsnCode(String startDate, String endDate);

    public StockDetails getStockItemDetails(String productId);

    public User getUser(String userName);
    public List<User> getUsers();
    public void saveStockDetails(StockDetails stockDetails);
    public void copyProductToStock(String dgst);
    public List<StockDetails> getStockDetailsByDgst(String dgst);
    public List<CompanyBillingSummary> getSalesDetails(String startDate,String endDate);
    public void saveUser(User user);
    public void getCnoAsCid();
    public void setPid();
    public void changeDateFormat();
    public void addDgst(String dgst);
    public void correctAmount();
    public List<BillAmountDetails> getBillsAmount(String dgst);
    public List<SumOfBillsAmount> getSumOfBillsAmount(String date);
    public void copyCustomers(String dgst);
    public void copyProducts(String dgst);
    public void copySoldStock(String dgst);
    public void copyCompanyDetails(String dgst);
    public void copyBillsAmount(String dgst);
    public void copyCustomerToBalanceDetails(String dgst);
    public void modifyBalanceDetails(BalanceDetails balanceDetails);
    List<BalanceDetails> getBalanceDetails(String dgst);
    public BalanceDetails getBalanceDetailsById(String id);
    }


