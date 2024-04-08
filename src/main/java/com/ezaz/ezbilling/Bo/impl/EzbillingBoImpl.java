package com.ezaz.ezbilling.Bo.impl;

import com.ezaz.ezbilling.Bo.EzbillingBo;
import com.ezaz.ezbilling.Util.PercentageUtils;
import com.ezaz.ezbilling.model.*;
import com.ezaz.ezbilling.repository.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class EzbillingBoImpl implements EzbillingBo {

    private final CustomerRepository customerRepository;
    private final GstCodeDetailsRepo gstCodeDetailsRepo;


    private final CompanyRepository companyRepository;

    private  final ProductRepository productRepository;

    private final ProductDetailsRepository productDetailsRepository;

    private final CompanyDetailsRepository companyDetailsRepository;

    private final CustomerRepositoryCustom customerRepositoryCustom;

    private final UsersRepository usersRepository;

    private final  BillingRepositry billingRepositry;

    private final BillItemsRepository billItemsRepository;

    private final PercentageUtils percentageUtils;

    public EzbillingBoImpl(CustomerRepository customerRepository, GstCodeDetailsRepo gstCodeDetailsRepo, CompanyRepository companyRepository, ProductRepository productRepository, ProductDetailsRepository productDetailsRepository, CompanyDetailsRepository companyDetailsRepository, CustomerRepositoryCustom customerRepositoryCustom, UsersRepository usersRepository, BillingRepositry billingRepositry, BillItemsRepository billItemsRepository, PercentageUtils percentageUtils) {
        this.customerRepository = customerRepository;
        this.gstCodeDetailsRepo = gstCodeDetailsRepo;
        this.companyRepository = companyRepository;
        this.productRepository = productRepository;
        this.productDetailsRepository = productDetailsRepository;
        this.companyDetailsRepository = companyDetailsRepository;
        this.customerRepositoryCustom = customerRepositoryCustom;
        this.usersRepository = usersRepository;
        this.billingRepositry = billingRepositry;
        this.billItemsRepository = billItemsRepository;
        this.percentageUtils = percentageUtils;
    }

    @Override
    public void saveCompanyDetails(CompanyDetails companyDetails) {
        companyRepository.save(companyDetails);
    }

    @Override
    public List<CompanyDetails> getCompanyDetails(String id) {
        return companyRepository.findAllByDgst(id);
    }

    @Override
    public void saveCustomerToDB(Customer customer) {
        Customer customerWithMaxId = customerRepository.maxCustomer();
        customer.setCno(customerWithMaxId.getCno()+1);
        customerRepository.save(customer);
    }

    @Override
    public List<GstCodeDetails> getGstCodeDetails() {
        return gstCodeDetailsRepo.findAll();
    }

    @Override
    public void saveProductDetails(ProductDetails productDetails) {
        productRepository.save(productDetails);
    }

    @Override
    public List<ProductDetails> getProductsDetails(String id) {
        return productRepository.findAllByDgst(id);
    }

    @Override
    public List<ProductNames> getProductNames(String id) {
        return productDetailsRepository.getProductNames(id);
    }

    @Override
    public Optional<ProductDetails> getProductDetailsByID(String id) {
        return productRepository.findById(id);
    }

    @Override
    public List<CompanyNames> getCompanyNames(String id) {
        return companyDetailsRepository.getCompanyNames(id);
    }

    @Override
    public Optional<CompanyDetails> getCompanyDetailsByID(String id) {
        return companyRepository.findById(id);
    }

    @Override
    public Optional<Customer> getCustomerDetailsByID(String id) {
        return customerRepository.findById(id);
    }

    @Override
    public List<CustomerNames> getCustomerNames(String id) {
        return customerRepositoryCustom.getCustomerNames(id);
    }

    @Override
    public List<ProductNames> getProductDetailsByCompany(String comapanyName) {
        return productRepository.findAllByPcom(comapanyName);
    }

    @Override
    public List<Customer> getCustomerByDgst(String dgst) {
        return customerRepository.findAllByDgst(dgst);
    }

    @Override
    public String saveBillItems(List<BillingDetails> billingDetailsList) {
          BillingDetails firstBillItem = billingDetailsList.get(0);

          User user= usersRepository.findById(firstBillItem.getDgst()).orElse(null);

          BillingDetails billingDetailsforMaxBillno = billingRepositry.getMaxBillNo(user.getId());


        LocalDate currentDate = LocalDate.now();

        // Define the desired date format (YYMM)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMM");

        // Format the current date using the formatter
        String formattedDate = currentDate.format(formatter);

          String newInvoiceNo=null;

          if(billingDetailsforMaxBillno==null){
              newInvoiceNo = user.getPrefix().toUpperCase(Locale.ROOT)+formattedDate+"01";
          }else {
              newInvoiceNo = Long.toString(Math.incrementExact(Long.parseLong(billingDetailsforMaxBillno.getBno(), 36)), 36);
          }
        for (BillingDetails billingDetails:billingDetailsList
             ) {
               billingDetails.setBno(newInvoiceNo);
               billingDetails.setAmount_after_disc((billingDetails.getAmount()*billingDetails.getQty())-percentageUtils.getPercentageAmount(billingDetails.getAmount()*billingDetails.getQty(),billingDetails.getDisc()));
               System.out.println(percentageUtils.getPercentageAmount(billingDetails.getAmount()*billingDetails.getQty(),billingDetails.getProduct_gst()));
               billItemsRepository.save(billingDetails);
        }




        return newInvoiceNo;
    }

    @Override
    public List<BillingDetails> getSavedBillDetailsByinvoiceNo(String invoiceNo) {
           Double total_tax=0.0;
           Double total_gross=0.0;
           Double total_amount=0.0;

        List<BillingDetails> savedBillDetails = billItemsRepository.findByBno(invoiceNo);

        for (BillingDetails billingDetails: savedBillDetails
             ) {
            ProductDetails productDetails = productRepository.findById(billingDetails.getProduct_name()).orElse(null);
            Customer customer = customerRepository.findById(billingDetails.getCno()).orElse(null);
            billingDetails.setProduct_name(productDetails.getPname());
            Float totalamountafterdiscount= billingDetails.getAmount()-percentageUtils.getPercentageAmount(billingDetails.getAmount(),billingDetails.getDisc());
            Float gross_amount=totalamountafterdiscount - percentageUtils.getPercentageAmount(totalamountafterdiscount,billingDetails.getProduct_gst());
            Float gst_amount =billingDetails.getAmount()-gross_amount;
            billingDetails.setGross_amount(gross_amount*billingDetails.getQty());
            billingDetails.setAmount(billingDetails.getAmount_after_disc());
            Float rate = billingDetails.getAmount()/billingDetails.getQty();
            System.out.println("rate "+rate);
            billingDetails.setRate(rate);
            System.out.println("billingDetails.getGross_amount() "+gross_amount);
            billingDetails.setGstamount(gst_amount);
            System.out.println("date "+billingDetails.getBilling_date());
        }
        return savedBillDetails;
    }

    @Override
    public BillDetails getBillDetailsByInvoiceNo(String invoiceNo) {

        List<BillingDetails> savedBillDetails = billItemsRepository.findByBno(invoiceNo);
        BillDetails billDetails = new BillDetails();
        billDetails.setDate(savedBillDetails.get(0).getBilling_date().toString());
        billDetails.setName(savedBillDetails.get(0).getCno());
        billDetails.setBno(savedBillDetails.get(0).getBno());
        List<ItemList> itemLists = new ArrayList<>();
        for(BillingDetails billingDetails:savedBillDetails){
            ItemList itemList = new ItemList();
            itemList.setRate(billingDetails.getAmount());
            itemList.setNoofunites(billingDetails.getQty());
            itemList.setPname(billingDetails.getProduct_name());
            itemList.setDisc(billingDetails.getDisc());
            itemLists.add(itemList);
        }
        billDetails.setItemList(itemLists);
        return billDetails;
    }

    @Override
    public String updateBillItems(List<BillingDetails> billingDetailsList) {
        try {
            BillingDetails firstBillItem = billingDetailsList.get(0);
            // Asynchronously delete existing bill items
            CompletableFuture<Void> deleteFuture = CompletableFuture.runAsync(() -> {
                billItemsRepository.deleteByBno(firstBillItem.getBno());
            });

            // Wait for the delete operation to complete
            deleteFuture.get(); // This will block until the delete operation is completed

            // Update and save new billing details
            for (BillingDetails billingDetails : billingDetailsList) {
                billingDetails.setAmount_after_disc((billingDetails.getAmount() * billingDetails.getQty()) - percentageUtils.getPercentageAmount(billingDetails.getAmount() * billingDetails.getQty(), billingDetails.getDisc()));
                System.out.println(percentageUtils.getPercentageAmount(billingDetails.getAmount() * billingDetails.getQty(), billingDetails.getProduct_gst()));
                billItemsRepository.save(billingDetails);
            }
            return firstBillItem.getBno();
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<GstReport> getGstDetailsByDate(String startDate, String endDate) throws ParseException {
        List<GstReport> gstReports = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<CustomerDetailswithGstNo> customerDetailswithGstNos = customerRepositoryCustom.findGstCustomers();

        for (CustomerDetailswithGstNo customerDetailswithGstNo : customerDetailswithGstNos) {
            List<sumOfGst> sumOfGsts = new ArrayList<>();
            GstReport gstReport = new GstReport();
            List<String> bills = new ArrayList<>();
            gstReport.setCustomerName(customerDetailswithGstNo.getCname());
            gstReport.setGstNo(customerDetailswithGstNo.getCtno());

            List<BillNo> billnos = billItemsRepository.findBnoByCnoAndBillingDateBetween(Integer.parseInt(customerDetailswithGstNo.getCno()), startDate, endDate);
            List<BillGstDetails> billGstDetailsList = new ArrayList<>();
            BillGstDetails billGstDetails = new BillGstDetails();
            for (BillNo bno : billnos) {


//                billGstDetails.setBno(bno.getBno());
                List<BillAggregationResult> billAggregationResults = billingRepositry.getGstDetails(bno.getBno());

                for (BillAggregationResult billAggregationResult : billAggregationResults) {
//                    BillGstDetails billGstDetails = new BillGstDetails();
//                    GstReport gstReport1 = checkBno(gstReports,billAggregationResult);
                        sumOfGst sumOfGst = new sumOfGst();
                        sumOfGst.setGst(billAggregationResult.getProduct_gst());
                        sumOfGst.setSumOfGstAmount(billAggregationResult.getTotalAmount());
                        sumOfGst.setBno(billAggregationResult.getBno());
                        sumOfGst.setBillingDate(billAggregationResult.getBillingDate());

                        // Check if the current sumOfGst already exists in the sumOfGsts list
                        boolean exists = false;
                        for (sumOfGst existingSum : sumOfGsts) {
                            if (existingSum.getBno().equals(sumOfGst.getBno()) && existingSum.getGst() == sumOfGst.getGst()) {
                                exists = true;
                                break;
                            }
                        }

//                         If it doesn't exist, add it to the list
                        if (!exists) {

                            sumOfGsts.add(sumOfGst);

                        }


                }


                bills.add(bno.getBno());
            }
            billGstDetails.setSumOfGsts(sumOfGsts);
            billGstDetailsList.add(billGstDetails);

            gstReport.setBillGstDetails(billGstDetailsList);
            if (gstReport.getCustomerName() != null || gstReport.getGstNo() != null) {

                gstReports.add(gstReport);
            }

        }

        return gstReports;
    }

//public GstReport checkBno(List<GstReport> gstReports,BillAggregationResult billAggregationResult){
//
//    for (GstReport gstReport:gstReports
//         ) {
//        if(gstReport.getBillGstDetails().getBno() == billAggregationResult.getBno()){
//            return gstReport;
//        }
//    }
//    return null;
//}

}