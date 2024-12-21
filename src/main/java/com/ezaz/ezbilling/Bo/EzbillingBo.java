package com.ezaz.ezbilling.Bo;

import com.ezaz.ezbilling.model.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;


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

    public String saveBillItems(List<BillingDetails> billingDetailsList) throws Exception;

    public SavedBillandWayBillDetails getSavedBillDetailsByinvoiceNo(String invoiceNo);

    public BillDetails getBillDetailsByInvoiceNo(String invoiceNo);

    public String updateBillItems(List<BillingDetails> billingDetailsList);

    public List<GstReport>  getGstDetailsByDate(String startDate, String endDate,String dgst) throws ParseException, IOException;
    public Set<String> getHsncodesNotAvaliable();

    public List<SoldStockSummary> getGstDetailsForHsnCode(String startDate, String endDate) throws IOException;

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
//    public void copyCustomers(String dgst);
//    public void copyProducts(String dgst);
//    public void copySoldStock(String dgst);
//    public void copyCompanyDetails(String dgst);
//    public void copyBillsAmount(String dgst);
    public void copyCustomerToBalanceDetails(String dgst);
    public void modifyBalanceDetails(BalanceDetails balanceDetails);
    List<BalanceDetails> getBalanceDetails(String dgst);
    public BalanceDetails getBalanceDetailsById(String id);
    public  List<SalesPerGST> getGstSalesOfGstCustomers(String fromDate, String toDate);
    public  List<SalesPerGST> getGstSalesOfCustomers(String fromDate, String toDate);
    public List<BillAmountDetails> getBillDetailsByDate(String date,String dgst);
    public void addCessandNetAmount(String date) throws ParseException;
    public List<MonthlySales> getSixMonthsSale();
    public List<MonthlySales> getSixMonthsSaleForCompanies(String company,int noOfMonths);
    public void setLegalName();
    public List<UqcAndDescription> getAllHsncodeDetails();
    public void addHsnCodeDetails(UqcAndDescription uqcAndDescription);
    public void deActivateCustomer(String customerId);
    public void deActivateProduct(String productId);
    public void deActivateCompany(String companyId);
    public List<ProductQty> getProductSales(String productCompany, String startDate, String endDate);
    public List<ProductSixMonthsSales> getProductSalesMontly(String productCompany, String productName, int numberOfMonths);

    }


