package com.ezaz.ezbilling.Bo.impl;

import com.ezaz.ezbilling.Bo.EzbillingBo;
import com.ezaz.ezbilling.Util.MongodbBackup;
import com.ezaz.ezbilling.Util.PercentageUtils;
import com.ezaz.ezbilling.model.*;
import com.ezaz.ezbilling.model.mysql.*;
import com.ezaz.ezbilling.repository.*;
import com.ezaz.ezbilling.repository.mysql.*;
import com.ezaz.ezbilling.services.ApiServices;
import com.ezaz.ezbilling.services.CsvExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EzbillingBoImpl implements EzbillingBo {

//    private static final Map<Integer, String> monthMap = new HashMap<>();
//
//    static {
//        monthMap.put(1, "January");
//        monthMap.put(2, "February");
//        monthMap.put(3, "March");
//        monthMap.put(4, "April");
//        monthMap.put(5, "May");
//        monthMap.put(6, "June");
//        monthMap.put(7, "July");
//        monthMap.put(8, "August");
//        monthMap.put(9, "September");
//        monthMap.put(10, "October");
//        monthMap.put(11, "November");
//        monthMap.put(12, "December");
//    }

    @Autowired
    private MongoTemplate mongoTemplate;

   @Autowired
   private MongodbBackup mongodbBackup;

   @Autowired
   private ApiServices apiServices;

   @Autowired
   private CsvExportService csvExportService;
    private final CustomerRepository customerRepository;
    private final GstCodeDetailsRepo gstCodeDetailsRepo;
    private final ChangeDateFormatRepo changeDateFormatRepo;
    private final BillAmountRepository billAmountRepository;


    private final CompanyRepository companyRepository;

    private  final ProductRepository productRepository;

    private final ProductDetailsRepository productDetailsRepository;

    private final CompanyDetailsRepository companyDetailsRepository;

    private final CustomerRepositoryCustom customerRepositoryCustom;

    private final UsersRepository usersRepository;

    private final  BillingRepositry billingRepositry;

    private final BillItemsRepository billItemsRepository;

    private final PercentageUtils percentageUtils;

    private final StockItemDetails stockItemDetails;

    private final StockRepository stockRepository;
    private final JpaCustomerRepo jpaCustomerRepo;
    private final JpaProductRepo jpaProductRepo;
    private final JpaSoldStockRepo jpaSoldStockRepo;
    private final JpaCompanyRepo jpaCompanyRepo;
    private final JpaBillsAmountRepo jpaBillsAmountRepo;
    private final BalanceDetailsRepository balanceDetailsRepository;
    private final UqcAndDescriptionRepository uqcAndDescriptionRepository;

    public EzbillingBoImpl(CustomerRepository customerRepository, GstCodeDetailsRepo gstCodeDetailsRepo, ChangeDateFormatRepo changeDateFormatRepo, BillAmountRepository billAmountRepository, CompanyRepository companyRepository, ProductRepository productRepository, ProductDetailsRepository productDetailsRepository, CompanyDetailsRepository companyDetailsRepository, CustomerRepositoryCustom customerRepositoryCustom, UsersRepository usersRepository, BillingRepositry billingRepositry, BillItemsRepository billItemsRepository, PercentageUtils percentageUtils, StockItemDetails stockItemDetails, StockRepository stockRepository, JpaCustomerRepo jpaCustomerRepo, JpaProductRepo jpaProductRepo, JpaSoldStockRepo jpaSoldStockRepo, JpaCompanyRepo jpaCompanyRepo, JpaBillsAmountRepo jpaBillsAmountRepo, BalanceDetailsRepository balanceDetailsRepository, UqcAndDescriptionRepository uqcAndDescriptionRepository) {

        this.customerRepository = customerRepository;
        this.gstCodeDetailsRepo = gstCodeDetailsRepo;
        this.changeDateFormatRepo = changeDateFormatRepo;
        this.billAmountRepository = billAmountRepository;
        this.companyRepository = companyRepository;
        this.productRepository = productRepository;
        this.productDetailsRepository = productDetailsRepository;
        this.companyDetailsRepository = companyDetailsRepository;
        this.customerRepositoryCustom = customerRepositoryCustom;
        this.usersRepository = usersRepository;
        this.billingRepositry = billingRepositry;
        this.billItemsRepository = billItemsRepository;
        this.percentageUtils = percentageUtils;
        this.stockItemDetails = stockItemDetails;
        this.stockRepository = stockRepository;
        this.jpaCustomerRepo = jpaCustomerRepo;
        this.jpaProductRepo = jpaProductRepo;
        this.jpaSoldStockRepo = jpaSoldStockRepo;
        this.jpaCompanyRepo = jpaCompanyRepo;
        this.jpaBillsAmountRepo = jpaBillsAmountRepo;
        this.balanceDetailsRepository = balanceDetailsRepository;

        this.uqcAndDescriptionRepository = uqcAndDescriptionRepository;
    }

    @Override
    public void saveCompanyDetails(CompanyDetails companyDetails) {

        companyDetails.setStatus("A");
        companyRepository.save(companyDetails);
    }

    @Override
    public List<CompanyDetails> getCompanyDetails(String id) {
        return companyRepository.findAllByDgstAndStatusNot(id,"D");
    }

    @Override
    public void saveCustomerToDB(Customer customer) {

        customer.setStatus("A");
        customerRepository.save(customer);

        if(!customer.getIsEdit()) {
            Customer customer1 = customerRepository.findByCname(customer.getCname());
            BalanceDetails balanceDetails = new BalanceDetails();
            balanceDetails.setCreditBalance(0.0);
            balanceDetails.setDebitBalance(0.0);
            balanceDetails.setTotalBalance(0.0);
            balanceDetails.setCname(customer.getCname());
            balanceDetails.setCno(customer.getId());
            balanceDetails.setDgst(customer1.getDgst());
            balanceDetailsRepository.save(balanceDetails);
        }
    }

    @Override
    public List<GstCodeDetails> getGstCodeDetails() {
        return gstCodeDetailsRepo.findAll();
    }

    @Override
    public void saveProductDetails(ProductDetails productDetails) {
        productDetails.setStatus("A");
        productRepository.save(productDetails);
        if(!productDetails.getIsEdit()) {
            ProductDetails productDetails1 = productRepository.findByPname(productDetails.getPname());
            StockDetails stockDetails = new StockDetails();
            stockDetails.setPid(productDetails1.getId());
            stockDetails.setPname(productDetails1.getPname());
            stockDetails.setIn_stock_units(0.0);
            stockDetails.setDgst(productDetails.getDgst());
            stockDetails.setPcom(productDetails.getPcom());
            stockItemDetails.save(stockDetails);
        }

    }

    @Override
    public List<ProductDetails> getProductsDetails(String id) {

        List<ProductDetails> productList = productRepository.findAllByDgst(id);

            return productList.stream()
                    .filter(product -> Optional.ofNullable(product.getStatus()).map(status -> !status.equals("D")).orElse(true))
                    .collect(Collectors.toList());

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
        return productRepository.findAllByPcomAndStatusNot(comapanyName, "D");

    }

    @Override
    public List<Customer> getCustomerByDgst(String dgst) {
        List<Customer> customerList =customerRepository.findAllByDgst(dgst);

        return customerList.stream()
                .filter(customer -> Optional.ofNullable(customer.getStatus()).map(status -> !status.equals("D")).orElse(true))
                .collect(Collectors.toList());
    }

    @Override
    public String saveBillItems(List<BillingDetails> billingDetailsList) throws Exception {
        Double totalBillAmount=0.0;
          BillingDetails firstBillItem = billingDetailsList.get(0);
          BillAmountDetails billAmountDetails= new BillAmountDetails();
          billAmountDetails.setCno(firstBillItem.getCno());
          billAmountDetails.setDgst(firstBillItem.getDgst());
          User user= usersRepository.findById(firstBillItem.getDgst()).orElse(null);
          int maxBillNoDeciamal=billingRepositry.findRecordWithHighestDecimal(user.getPrefix());
        LocalDate currentDate = LocalDate.now();
        // Define the desired date format (YYMM)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy");
        // Format the current date using the formatter
        String formattedDate = currentDate.format(formatter);

          String newInvoiceNo=null;

          if(maxBillNoDeciamal==0){
              newInvoiceNo = user.getPrefix()+formattedDate+"001";
          }else {
              newInvoiceNo = user.getPrefix()+formattedDate+(maxBillNoDeciamal+1);
          }
          billAmountDetails.setBno(newInvoiceNo);
          billAmountDetails.setDate(firstBillItem.getBilling_date());

        for (BillingDetails billingDetails:billingDetailsList
             ) {

            StockDetails stockDetails = getStockItemDetails(billingDetails.getProduct_name());

            StockDetails newStockDetails = new StockDetails();
            newStockDetails.set_id(stockDetails.get_id());
            newStockDetails.setIn_stock_units(stockDetails.getIn_stock_units()-billingDetails.getQty());
            stockRepository.updateBillItemInStock(newStockDetails);

            ProductDetails productDetails= productRepository.findById(billingDetails.getProduct_name()).orElse(null);
            float deductablePer=billingDetails.getProduct_gst()+productDetails.getCess();
            Double rateAfter = (billingDetails.getAmount()/((deductablePer)+100))*100;
            Double cessAmount= percentageUtils.getPercentageAmount(rateAfter,productDetails.getCess());
               billingDetails.setCess(productDetails.getCess());
               billingDetails.setNetAmount(rateAfter*billingDetails.getQty());
               billingDetails.setCessAmount(cessAmount*billingDetails.getQty());
               billingDetails.setBilling_date(billingDetails.getBilling_date().substring(0,10));
               billingDetails.setBno(newInvoiceNo);
               billingDetails.setAmount_after_disc((billingDetails.getAmount()*billingDetails.getQty()));
               billItemsRepository.save(billingDetails);
               totalBillAmount = totalBillAmount+billingDetails.getAmount()*billingDetails.getQty();
        }
            billAmountDetails.setAmount(totalBillAmount);
            billAmountRepository.save(billAmountDetails);
        BalanceDetails balanceDetails = balanceDetailsRepository.findByCno(firstBillItem.getCno());
        balanceDetails.setReason("invoice added");
        balanceDetails.setType("Credit");
        Double balanceAmount=balanceDetails.getTotalBalance()+totalBillAmount;
        balanceDetails.setTotalBalance(balanceAmount);
        balanceDetails.setLastUpdatedDate(firstBillItem.getBilling_date());
        balanceDetails.setLastUpdateAmount(totalBillAmount);
        if(firstBillItem.getCno()==null && newInvoiceNo==null){
            if(firstBillItem.getCno()==null){
                throw new Exception("no customer id");
            }
            if(newInvoiceNo==null){
                throw new Exception("Invoice number not generated");
            }

        }
        balanceDetailsRepository.save(balanceDetails);
//        mongodbBackup.BackUp();
        return newInvoiceNo;
    }

    @Override
    public SavedBillandWayBillDetails getSavedBillDetailsByinvoiceNo(String invoiceNo) {

          SavedBillandWayBillDetails savedBillandWayBillDetails = new SavedBillandWayBillDetails();

        List<BillingDetails> savedBillDetails = billItemsRepository.findByBno(invoiceNo);

        for (BillingDetails billingDetails: savedBillDetails
             ) {
            ProductDetails productDetails = productRepository.findById(billingDetails.getProduct_name()).orElse(null);
            billingDetails.setProduct_name(productDetails.getPname());
            billingDetails.setCess(productDetails.getCess());
            float per =productDetails.getVatp()+productDetails.getCess();
            Double rateAfter = (billingDetails.getAmount()/((per)+100))*100;
//            Double cessAmount= percentageUtils.getPercentageAmount(rateAfter,productDetails.getCess());
            Double cessAmount= billingDetails.getCessAmount();
            Double gst_amount =percentageUtils.getPercentageAmount(rateAfter,productDetails.getVatp());
            Double actualCostBeforeGstAndDiscount = billingDetails.getAmount()-(cessAmount+gst_amount);
            Double actualAmountAfterDisc= actualCostBeforeGstAndDiscount - percentageUtils.getPercentageAmount(actualCostBeforeGstAndDiscount,billingDetails.getDisc());
            Double cessAmountAfterDisc= percentageUtils.getPercentageAmount(rateAfter,productDetails.getCess())*billingDetails.getQty();
            Double gstAmountAfterDisc=percentageUtils.getPercentageAmount(rateAfter,productDetails.getVatp())*billingDetails.getQty();
            billingDetails.setCessAmount(cessAmount);
            billingDetails.setGstamount(gst_amount*billingDetails.getQty());
            billingDetails.setGross_amount(rateAfter*billingDetails.getQty());
            billingDetails.setRate(billingDetails.getAmount());
            Double totalamountafterdiscount= billingDetails.getAmount()-percentageUtils.getPercentageAmount(billingDetails.getAmount(),billingDetails.getDisc())-cessAmount;
            Double gross_amount=totalamountafterdiscount - percentageUtils.getPercentageAmount(totalamountafterdiscount,billingDetails.getProduct_gst());
            billingDetails.setTotal_amount((rateAfter+gst_amount+cessAmount)*billingDetails.getQty());
            billingDetails.setAmount(billingDetails.getAmount()*billingDetails.getQty());
//            billingDetails.setAmount((rateAfter+gst_amount+cessAmount)*billingDetails.getQty());
            billingDetails.setAmount_after_disc((rateAfter+gst_amount+cessAmount)*billingDetails.getQty());
        }
        List<WayBillDetails> wayBillDetails = calculateWayBillDetails(savedBillDetails);

        savedBillandWayBillDetails.setWayBillDetails(wayBillDetails);
        savedBillandWayBillDetails.setBillingDetails(savedBillDetails);

        return savedBillandWayBillDetails;
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
            ProductDetails productDetails =productRepository.findById(billingDetails.getProduct_name()).orElse(null);
            itemList.setPname(productDetails.getPname());
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
            List<BillingDetails> savedBillDetails = billItemsRepository.findByBno(firstBillItem.getBno());
            // Asynchronously delete existing bill items
            CompletableFuture<Void> deleteFuture = CompletableFuture.runAsync(() -> {
                billItemsRepository.deleteByBno(firstBillItem.getBno());
            });

            // Wait for the delete operation to complete
            deleteFuture.get(); // This will block until the delete operation is completed
            Double totalAmount=0.0;
            // Update and save new billing details
            for (BillingDetails billingDetails : billingDetailsList) {
                Optional<BillingDetails> result = savedBillDetails.stream()
                        .filter(billingDetails1 -> billingDetails1.getProduct_name().equals(billingDetails.getProduct_name()))
                        .findFirst();

                if (result.isPresent()) {
                    BillingDetails foundBillingDetails = result.get();
                    if(foundBillingDetails.getQty()>billingDetails.getQty()){
                        StockDetails stockDetails = getStockItemDetails(billingDetails.getProduct_name());

                        StockDetails newStockDetails = new StockDetails();
                        newStockDetails.set_id(stockDetails.get_id());
                        int differenceUnits= foundBillingDetails.getQty()-billingDetails.getQty();
                        newStockDetails.setIn_stock_units(stockDetails.getIn_stock_units()+differenceUnits);
                        stockRepository.updateBillItemInStock(newStockDetails);

                    }else if(foundBillingDetails.getQty()<billingDetails.getQty()){
                        StockDetails stockDetails = getStockItemDetails(billingDetails.getProduct_name());
                        int differenceUnits= billingDetails.getQty()-foundBillingDetails.getQty();
                        StockDetails newStockDetails = new StockDetails();
                        newStockDetails.set_id(stockDetails.get_id());
                        newStockDetails.setIn_stock_units(stockDetails.getIn_stock_units()-differenceUnits);
                        stockRepository.updateBillItemInStock(newStockDetails);

                    }

                } else {


                    StockDetails stockDetails = getStockItemDetails(billingDetails.getProduct_name());

                    StockDetails newStockDetails = new StockDetails();
                    newStockDetails.set_id(stockDetails.get_id());
                    newStockDetails.setIn_stock_units(stockDetails.getIn_stock_units()-billingDetails.getQty());
                    stockRepository.updateBillItemInStock(newStockDetails);
                }
                for (BillingDetails savedDetail : savedBillDetails) {
                    Optional<BillingDetails> result1 = billingDetailsList.stream()
                            .filter(billingDetails1 -> billingDetails1.getProduct_name().equals(savedDetail.getProduct_name()))
                            .findFirst();
                    if(!result1.isPresent()){
                        StockDetails stockDetails = getStockItemDetails(savedDetail.getProduct_name());
                        StockDetails newStockDetails = new StockDetails();
                        newStockDetails.set_id(stockDetails.get_id());
                        newStockDetails.setIn_stock_units(stockDetails.getIn_stock_units()+billingDetails.getQty());
                        stockRepository.updateBillItemInStock(newStockDetails);
                    }

                }

                billingDetails.setAmount_after_disc(billingDetails.getAmount() * billingDetails.getQty());
                ProductDetails productDetails= productRepository.findById(billingDetails.getProduct_name()).orElse(null);
                float deductablePer =billingDetails.getProduct_gst()+productDetails.getCess();
                Double rateAfter = (billingDetails.getAmount()/((deductablePer)+100))*100;
                billingDetails.setCess(productDetails.getCess());
                billingDetails.setNetAmount(rateAfter*billingDetails.getQty());
                Double cessAmount= percentageUtils.getPercentageAmount(rateAfter,productDetails.getCess());
                billingDetails.setCessAmount(cessAmount*billingDetails.getQty());
                billItemsRepository.save(billingDetails);
                totalAmount=totalAmount+billingDetails.getAmount() * billingDetails.getQty();
            }
            BillAmountDetails billAmountDetails = billAmountRepository.findByBno(firstBillItem.getBno());
            BalanceDetails existingBalanceDetails = balanceDetailsRepository.findByCno(firstBillItem.getCno());
            String cusNo= billAmountDetails.getCno();
            Double existingBalance= existingBalanceDetails.getTotalBalance();
            if(firstBillItem.getCno().equals(billAmountDetails.getCno())) {
                existingBalanceDetails.setReason("Invoice Updated");
                if (billAmountDetails.getAmount() > totalAmount) {
                    existingBalanceDetails.setType("Debit");
                    Double differenceAmount = billAmountDetails.getAmount() - totalAmount;
                    billAmountDetails.setAmount(billAmountDetails.getAmount() - differenceAmount);
                    billAmountRepository.save(billAmountDetails);
                    existingBalanceDetails.setTotalBalance(existingBalance - differenceAmount);
                    existingBalanceDetails.setLastUpdatedDate(firstBillItem.getBilling_date());
                    existingBalanceDetails.setLastUpdateAmount(differenceAmount);
                    balanceDetailsRepository.save(existingBalanceDetails);
                } else if (billAmountDetails.getAmount() < totalAmount) {
                    existingBalanceDetails.setType("Credit");
                    Double differenceAmount = totalAmount - billAmountDetails.getAmount();
                    billAmountDetails.setAmount(billAmountDetails.getAmount() + differenceAmount);
                    billAmountRepository.save(billAmountDetails);
                    existingBalanceDetails.setTotalBalance(existingBalance + differenceAmount);
                    existingBalanceDetails.setLastUpdatedDate(firstBillItem.getBilling_date());
                    existingBalanceDetails.setLastUpdateAmount(differenceAmount);
                    balanceDetailsRepository.save(existingBalanceDetails);
                }
            }else{
                billAmountDetails.setCno(firstBillItem.getCno());
                billAmountDetails.setAmount(totalAmount);
                billAmountRepository.save(billAmountDetails);

                BalanceDetails existingBalanceDetailsOfCurrentCus = balanceDetailsRepository.findByCno(cusNo);
                existingBalanceDetailsOfCurrentCus.setReason("invoice corrected");
                existingBalanceDetailsOfCurrentCus.setType("Debit");
                existingBalanceDetailsOfCurrentCus.setLastUpdatedDate(firstBillItem.getBilling_date());
                existingBalanceDetailsOfCurrentCus.setLastUpdateAmount(totalAmount);
                existingBalanceDetailsOfCurrentCus.setTotalBalance(existingBalanceDetailsOfCurrentCus.getTotalBalance()-totalAmount);
                balanceDetailsRepository.save(existingBalanceDetailsOfCurrentCus);

                BalanceDetails existingBalanceDetailsOfUpdatedCus = balanceDetailsRepository.findByCno(firstBillItem.getCno());
                existingBalanceDetailsOfUpdatedCus.setType("Credit");
                existingBalanceDetailsOfUpdatedCus.setReason("invoice swapped from "+ existingBalanceDetailsOfCurrentCus.getCname());
                existingBalanceDetailsOfUpdatedCus.setLastUpdateAmount(totalAmount);
                existingBalanceDetailsOfUpdatedCus.setLastUpdatedDate(firstBillItem.getBilling_date());
                existingBalanceDetailsOfUpdatedCus.setTotalBalance(existingBalanceDetailsOfUpdatedCus.getTotalBalance()+totalAmount);
                balanceDetailsRepository.save(existingBalanceDetailsOfUpdatedCus);


            }
            return firstBillItem.getBno();
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<GstReport> getGstDetailsByDate(String startDate, String endDate,String dgst) throws ParseException, IOException {
        List<GstReport> gstReports = new ArrayList<>();
        List<CustomerDetailswithGstNo> customerDetailswithGstNos = customerRepositoryCustom.findGstCustomers();

        for (CustomerDetailswithGstNo customerDetailswithGstNo : customerDetailswithGstNos) {
            GstReport gstReport = new GstReport();
            gstReport.setCustomerName(customerDetailswithGstNo.getCname());
            gstReport.setGstNo(customerDetailswithGstNo.getCtno());

            List<BillNo> billnos = billItemsRepository.findBnoByCnoAndBillingDateBetween(customerDetailswithGstNo.getId(), startDate, endDate);

            Map<String, BillGstDetails> billGstDetailsMap = new HashMap<>();
            for (BillNo bno : billnos) {
                BillGstDetails billGstDetails = new BillGstDetails();
                billGstDetails.setSumOfGsts(new ArrayList<>());

                Set<Integer> addedGstValues = new HashSet<>(); // To keep track of added GST values for each BillGstDetails

                List<BillAggregationResult> billAggregationResults = billingRepositry.getGstDetails(bno.getBno());

                Double totalSum = 0.0;
                Double totalTaxableSum = 0.0;

                for (BillAggregationResult billAggregationResult : billAggregationResults) {
                    sumOfGst sumOfGst = new sumOfGst();
                    sumOfGst.setGst(billAggregationResult.getProduct_gst());
                    Double rateAfter = (billAggregationResult.getNetAmount());
                    sumOfGst.setSumOfGstAmount(rateAfter);
                    sumOfGst.setBno(billAggregationResult.getBno());
                    sumOfGst.setBillingDate((billAggregationResult.getBillingDate()).substring(0,10));
                    sumOfGst.setSumOfCessAmount(billAggregationResult.getTotalCessAmount());

                    // Check if the current GST value has already been added for this BillGstDetails
                    if (!addedGstValues.contains(sumOfGst.getGst())) {
                        addedGstValues.add(sumOfGst.getGst());

                        billGstDetails.getSumOfGsts().add(sumOfGst);
                        totalSum += billAggregationResult.getTotalAmount(); // Calculate total sum of GST amounts
                        totalTaxableSum += rateAfter; // Calculate total taxable sum
                    }
                }

                // Set total sum of GST amounts and taxable sum for the current BillGstDetails
                billGstDetails.setTotalofSum(totalSum);
                billGstDetails.setTotalTaxableSum(totalTaxableSum);
                billGstDetailsMap.put(bno.getBno(), billGstDetails);
            }

            List<BillGstDetails> billGstDetailsList = new ArrayList<>(billGstDetailsMap.values());
            gstReport.setBillGstDetails(billGstDetailsList);

            if (gstReport.getCustomerName() != null || gstReport.getGstNo() != null) {
                gstReports.add(gstReport);
            }
        }
        List<String[]> gstDetailsList = new ArrayList<>();

        for (GstReport gstReport1:gstReports
        ) {
            Customer customer = customerRepository.findByCname(gstReport1.getCustomerName());
            for (BillGstDetails billGstDetails1:gstReport1.getBillGstDetails()
                 ) {
                for (sumOfGst sumOfGst1: billGstDetails1.getSumOfGsts()
                     ) {
                    String[] gstDetails = new String[]{customer.getLegal_name(),sumOfGst1.getBno(),convertDate(sumOfGst1.getBillingDate()),String.format("%.2f", getTotalInvoiceValue(sumOfGst1.getSumOfGstAmount(),sumOfGst1.getGst(),sumOfGst1.getSumOfCessAmount())),customer.getSupplyplace(),
                    "N",null,getInvoiceType(customer.getSupplyplace(),dgst),null,sumOfGst1.getGst().toString(),String.format("%.2f", sumOfGst1.getSumOfGstAmount()),String.format("%.2f", sumOfGst1.getSumOfCessAmount())};

                    gstDetailsList.add(gstDetails);

                }
            }

        }
        String[] gstDetailsHeaders = {
                "Receiver Name", "Invoice Number", "Invoice Date", "Invoice Value",
                "Place Of Supply", "Reverse Charge", "Applicable % of Tax Rate",
                "Invoice Type", "E-Commerce GSTIN", "Rate", "Taxable Value", "Cess Amount"
        };
        String documentName="b2b_sez_de.csv";
        csvExportService.generateGstDetailsCsvFile(gstDetailsList,gstDetailsHeaders,documentName);
        return gstReports;
    }
    public Double getTotalInvoiceValue(Double taxableAmount,int gstPer,Double cessAmount){
        Double gstAmount = percentageUtils.getPercentageAmount(taxableAmount,gstPer);
        return taxableAmount+gstAmount+cessAmount;
    }
    public String getInvoiceType(String supplyPlace, String dgst){
        User user = usersRepository.findById(dgst).orElse(null);
            // Split the string at the hyphen and get the part after it
            String[] parts = supplyPlace.split("-");
                // Compare the part after the hyphen with str2
               if(parts[1].equals(user.getState())){
                   return "Regular B2B";
               }else{
                   return "Intra-State supplies attracting IGST";
               }
    }
    public static String convertDate(String inputDate) {
        // Define the input and output date formats
        String trimmedDate= inputDate.substring(0, 10);
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yy", Locale.ENGLISH);

            // Parse the input date string to LocalDate
            LocalDate date = LocalDate.parse(trimmedDate, inputFormatter);

            // Format the date to the desired output format
            return date.format(outputFormatter).toLowerCase();
    }

    public List<SoldStockSummary> getGstDetailsForHsnCode(String startDate, String endDate) throws IOException {

        List<SoldStockSummary> soldStockSummaries =billingRepositry.getSoldStockSummary(startDate,endDate);
        List<SoldStockSummary> soldStockSummariesWithIgst =billingRepositry.getSoldStockSummaryForHsncode(startDate,endDate);

        List<SoldStockSummary> finalSoldStockSummaries = Stream.concat(soldStockSummaries.stream(), soldStockSummariesWithIgst.stream())
                .collect(Collectors.toList());

        String[] hsnDetailsHeaders = {
                "HSN", "Description", "UQC", "Total Quantity", "Total Value", "Taxable Value", "Integrated Tax Amount",
                "Central Tax Amount", "State/UT Tax Amount", "Cess Amount", "Rate"
        };
        List<String[]> hsnDetailsDataList= new ArrayList<>();
        for (SoldStockSummary soldStockSummary1:finalSoldStockSummaries
             ) {
            UqcAndDescription uqcAndDescription = getHsnDiscription(soldStockSummary1.getHsn_code());
            String[] hsnDetailsData = new String[]{soldStockSummary1.getHsn_code(),
                    uqcAndDescription.getHsnDescription(),
                    uqcAndDescription.getUqc(),
                    String.valueOf(soldStockSummary1.getTotalQty())
            ,String.format("%.2f", (soldStockSummary1.getTaxableAmount()+soldStockSummary1.getTaxAmount()+soldStockSummary1.getTotalCessAmount())),
                    String.valueOf(soldStockSummary1.getTaxableAmount()),
                    String.valueOf(soldStockSummary1.getIgst())
            ,String.valueOf(soldStockSummary1.getCgst()),
                    String.valueOf(soldStockSummary1.getSgst()),
                    String.valueOf(soldStockSummary1.getTotalCessAmount())
            ,String.valueOf(soldStockSummary1.getProduct_gst())};
            hsnDetailsDataList.add(hsnDetailsData);
        }
        csvExportService.generateGstDetailsCsvFile(hsnDetailsDataList,hsnDetailsHeaders,"hsn.csv");

        return finalSoldStockSummaries;

    }

  public UqcAndDescription getHsnDiscription(String hsnCode){
        UqcAndDescription uqcAndDescription =uqcAndDescriptionRepository.findByHsnCode(hsnCode);
        if (uqcAndDescription == null){
            uqcAndDescription =uqcAndDescriptionRepository.findByHsnCode("0"+hsnCode);
        }
        return uqcAndDescription;
  }

    public StockDetails getStockItemDetails(String productId) {
        return stockItemDetails.findByPid(productId);
    }

    public void saveStockDetails(StockDetails stockDetails){
        StockDetails stockDetail= new StockDetails();
        stockDetail.set_id(stockDetails.get_id());
        stockDetail.setPid(stockDetails.getPid());
        stockDetail.setIn_stock_units(stockDetails.getIn_stock_units()+stockDetails.getNewStock());
        stockDetail.setPname(stockDetails.getPname());
        stockDetail.setLast_updated_date(stockDetails.getLast_updated_date());
        stockRepository.updateStock(stockDetail);;
    }



    public void copyProductToStock(String dgst){
        List<ProductDetails> productDetails= productRepository.findAllByDgst(dgst);

        for (ProductDetails productDetail: productDetails
             ) {
            StockDetails stockDetails = new StockDetails();
            stockDetails.setPid(productDetail.getId());
            stockDetails.setPname(productDetail.getPname());
            stockDetails.setIn_stock_units(0.00);
            stockDetails.setDgst(dgst);
            stockDetails.setPcom(productDetail.getPcom());
            stockItemDetails.save(stockDetails);

        }
    }

    public List<StockDetails> getStockDetailsByDgst(String dgst){
        List<StockDetails> stockDetails = stockItemDetails.findByDgst(dgst);
        return stockDetails;
    }

    public List<CompanyBillingSummary> getSalesDetails(String startDate,String endDate){
        return billingRepositry.getCompanyBillingSummary(startDate,endDate);
    }

    public void saveUser(User user) {
        User user1 = usersRepository.findByUsername(user.getUsername());
        if (user1 != null) {
            user1.setUsername(user.getUsername());
            user1.setPassword(user.getPassword());
            user1.setRole(user.getRole());
            user1.setAddress(user.getAddress());
            user1.setGstNo(user.getGstNo());
            user1.setFirmName(user.getFirmName());
            user1.setContact(user.getContact());
            user1.setPrefix(user.getPrefix());
            user1.setState(user.getState());
            user1.setVehicalNo(user.getVehicalNo());
            // Get the collection using mongoTemplate
            String collectionName = "Users"; // adjust the collection name as needed
            mongoTemplate.updateFirst(
                    Query.query(Criteria.where("username").is(user1.getUsername())),
                    new Update()
                            .set("username", user1.getUsername())
                            .set("password", user1.getPassword())
                            .set("role", user1.getRole())
                            .set("prefix", user1.getPrefix())
                            .set("address", user1.getAddress())
                            .set("firmName", user1.getFirmName())
                            .set("gstNo", user1.getGstNo())
                            .set("contact", user1.getContact())
                            .set("state", user1.getState())
                            .set("vehicalNo", user1.getVehicalNo()),
                    collectionName);


        }else {
            usersRepository.save(user);
        }
    }

    public List<User> getUsers(){
        return usersRepository.findAll();
    }

    public User getUser(String userName){
        return usersRepository.findByUsername(userName);
    }

    public void getCnoAsCid(){
        List<Customer> customerList= customerRepository.findAll();
        for (Customer customer:customerList
             ) {
            List<BillingDetails> billingDetailsList = billItemsRepository.findByCno(customer.getCno());
            for (BillingDetails billingDetails:billingDetailsList
                 ) {
                billingDetails.setCno(customer.getId());
                billItemsRepository.save(billingDetails);
            }

        }
    }

    public void setPid(){
        List<ProductDetails> productDetails= productRepository.findAll();
        List<BillAmountDetails> billAmountDetails = billAmountRepository.findAll();
        for (BillAmountDetails amountDetails : billAmountDetails
        ){
        Customer customer = customerRepository.findByCno(amountDetails.getCno());
        if(customer !=null) {
            amountDetails.setCno(customer.getId());
            billAmountRepository.save(amountDetails);
        }
        }
        for (ProductDetails productDetails1:productDetails
             ) {
            List<BillingDetails> billingDetailsList = billingRepositry.getBillingDetailsByPname(productDetails1.getPname());

            for (BillingDetails billingDetails:billingDetailsList
                 ) {
                billingDetails.setProduct_name(productDetails1.getId());
                billItemsRepository.save(billingDetails);
            }
        }
    }
    public void changeDateFormat(){
        List<BillingItemDetails> billingDetailsList = changeDateFormatRepo.findAll();
        for (BillingItemDetails billingDetails : billingDetailsList
        ){
        String convertedDate = convertToISO8601(billingDetails.getBilling_date());
        if(convertedDate!=null)
        billingDetails.setBilling_date(convertedDate);

            changeDateFormatRepo.save(billingDetails);


        }
    }

    public static String convertToISO8601(String dateString) {
        DateFormat originalFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        originalFormat.setTimeZone(TimeZone.getTimeZone("IST")); // Setting input timezone
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        targetFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Setting output timezone

        try {
            Date date = originalFormat.parse(dateString);
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Handle parse exception
        }
    }

    public void addDgst(String dgst){
        billingRepositry.addDgst(dgst);
        companyDetailsRepository.addDgst(dgst);
        customerRepository.addDgst(dgst);
        productDetailsRepository.addDgst(dgst);
        stockRepository.addDgst(dgst);
    }
   public void correctAmount(){
        List<BillingDetails> billingDetailsList= billItemsRepository.findAll();
       for (BillingDetails billingDetails:billingDetailsList
            ) {
           Double correctedAmount= billingDetails.getAmount()/billingDetails.getQty();
           billingDetails.setAmount(correctedAmount);
           billItemsRepository.save(billingDetails);

       }
   }

   public List<BillAmountDetails> getBillsAmount(String dgst){
       List<BillAmountDetails> billAmountDetails= billAmountRepository.findAllByDgst(dgst);
       List<BillAmountDetails> billAmountDetailsWithCustomerNames= new ArrayList<>();
       for (BillAmountDetails amountDetails :billAmountDetails
            ) {
           Customer customer = customerRepository.findById(amountDetails.getCno()).orElse(null);
           amountDetails.setCno(customer.getCname());
           billAmountDetailsWithCustomerNames.add(amountDetails);
       }
       return billAmountDetailsWithCustomerNames;
   }

    public List<BillAmountDetails> getBillDetailsByDate(String date,String dgst){
        List<BillAmountDetails> billAmountDetails= billAmountRepository.findByDateAndDgst(date,dgst);
        List<BillAmountDetails> billAmountDetailsWithCustomerNames= new ArrayList<>();
        for (BillAmountDetails amountDetails :billAmountDetails
        ) {
            Customer customer = customerRepository.findById(amountDetails.getCno()).orElse(null);
            amountDetails.setCno(customer.getCname());
            billAmountDetailsWithCustomerNames.add(amountDetails);
        }
        return billAmountDetailsWithCustomerNames;
    }
   public List<SumOfBillsAmount> getSumOfBillsAmount(String date){
        return billingRepositry.getAggregatedResults(date);
   }



   public void copyCustomers(String dgst){
         List<JpaCustomer> jpaCustomers=jpaCustomerRepo.findAll();
       for (JpaCustomer jpaCustomer : jpaCustomers
       ){
           Customer customer = new Customer();
           customer.setCno(String.valueOf(jpaCustomer.getCno()));
           customer.setCadd(jpaCustomer.getCadd());
           customer.setCname(jpaCustomer.getCname());
           customer.setDgst(dgst);
           customer.setIsigst(jpaCustomer.getIsigst());
           customer.setCpno(jpaCustomer.getCpno());
           customer.setLegal_name(jpaCustomer.getLegal_name());
           customer.setSupplyplace(jpaCustomer.getSupplyplace());
           customer.setCtno(jpaCustomer.getCtno());
           customerRepository.save(customer);
       }

   }

   public void copyProducts(String dgst){
   List<JpaProduct> jpaProducts= jpaProductRepo.findAll();
       for (JpaProduct jpaProduct:jpaProducts
            ) {
           ProductDetails productDetails = new ProductDetails();
           productDetails.setPname(jpaProduct.getPname().trim());
           productDetails.setDgst(dgst);
           productDetails.setCess(0);
           productDetails.setRate(jpaProduct.getRate());
           productDetails.setPcom(jpaProduct.getPcom());
           productDetails.setHsn_code(jpaProduct.getHsnCode());
           productDetails.setRel_prod(jpaProduct.getRelProd());
           productDetails.setIs_sp(jpaProduct.getIsSp());
           productDetails.setMrp(jpaProduct.getMrp());
           productDetails.setVatp(jpaProduct.getVatp());
           productDetails.setUnites_per(jpaProduct.getUnitesPer());
           productDetails.setNo_of_unites(jpaProduct.getNoOfUnites());
           productRepository.save(productDetails);
       }



   }
    public void copySoldStock(String dgst){
    List<JpaSoldStock> jpaSoldStocks = jpaSoldStockRepo.findAllDistinct();
        for (JpaSoldStock jpaSoldStock:jpaSoldStocks
             ) {
            BillingDetails billingDetails = new BillingDetails();
            billingDetails.setBilling_date(jpaSoldStock.getBillingDate().toString());
            Customer customer = customerRepository.findByCno(jpaSoldStock.getCno().toString());
            billingDetails.setCno(customer.getId());
            ProductDetails productDetails= productRepository.findByPname(jpaSoldStock.getProductName().trim());
            if(productDetails==null){
                Map<String, String> products = new HashMap<>();


                products.put("Lemon rice 5/-", "Lemon rice 5/- (40pcs)");
                products.put("Chilly 25G 10/-", "Chilly Powder 10/-(40pcs)");
                products.put("Chilly 500G", "Chilly Powder 500G");
                products.put("Chilly 25G 10/-(40pcs)", "Chilly Powder 10/-(40pcs)");
                products.put("Chilly Powder 500G ", "Chilly Powder 500G");
                products.put("Samar 100G offer", "Sambar 100G offer");
                products.put("Apis Honey 25g (24pcs)", "Apis Honey 20g (24pcs)");
                products.put("AACHI CHICKEN 5/-(600P)", "AACHI CHICKEN MASALA 5/-(600P)");
                products.put("AACHI GARAM 5/-(600P)", "AACHI GARAM MASALA 5/-(600P)");
                products.put("AACHI MUTTON  5/-(1200P)", "AACHI MUTTON MASALA  5/-(1200P)");
                products.put("TF-GGP 5/-", "TF-GGP 5/- (30p)");
                products.put("fru str jelly", "fru str jelly");
                products.put("Lemon rice 5/- ", "Lemon rice 5/- (40pcs)");
                products.put("fru str jelly ", "fru str jelly");
                products.put("TF-GGP 5/- (30p) ", "TF-GGP 5/- (30p)");
                products.put("Puliyogare 10/- ", "Puliyogare 10/- ");
                products.put("Chilly 500G ", "Chilly Powder 10/-(40pcs)");
                products.put("Xtra Dish Wash Powder ", "Xtra Dish Wash Powder ");
                products.put("Chilly powder 50g ", "Chilly powder 50g ");
                products.put("Samar powder 500G", "Sambar powder 500G");
                products.put("AACHI PEPER 6/-(600P)", "AACHI PEPER POWDER 6/-(600P)");
                products.put("Aachi Chicken Masala 10/-(60pcs)", "Aachi Chicken Masala 10/-(50pcs)");
                products.put("Aachi Mutton Masala 10/-(60pcs)", "Aachi Mutton Masala 10/-(50pcs)");
                products.put("Aachi Biryani Masala 15/- (60pcs)", "Aachi Biryani Masala 15/- (50pcs)");
                products.put("Aachi Garam Masala 10/-(60pcs)", "Aachi Garam Masala 10/-(50pcs)");
                products.put("KESAR BADAM pillow 48x150g", "KESAR BADAM pillow 48x200g");
                products.put("OAT MEAL DIGESTIVE 1+1 PROMO", "oat meal digestive 1+1*(36pcs )");
                products.put("AF PET 500ml", "AF PET 600ml");
                products.put("ATHASIYA DHOOP", "ATHISAYA DHOOP");
                products.put("Ambica darbar 20g", "Ambica darbar 35g");
                products.put("naviedya 72", "Naivedya 72");
                products.put("CHOCORIPPLEpillow48x2000g", "CHOCORIPPLEpillow48x200g");
                products.put("Unibic chocochip150gdp", "Unibic chocochip150g dp");
                products.put("OAT MEAL 12x16x37.5g", "OAT MEAL 12x16x50g");
                products.put("AMR Back pain rollon", "AMR Back pain rollon 50ml");
                products.put("NO-1", "NO-1 TEA");
                products.put("Bakingpowder 500g", "Bakingpowder 50g");
                products.put("naviedya 32", "Naivedya 32");
                products.put("Amchoor powder68100g", "Amchoor powder100g");
                products.put("sofit ltr", "SOFIT 1 LTR (V) *");
                products.put("caramella Lolipop-2/-", "Caramella Lolipop-2/-");
                products.put("OMD 1+1 48 PIECES", "Unibic omd10/-");
                products.put("sugar free choco swrill 75g", "sugarfree chocoswrill75g");
                products.put("Xtra.det.kingsize 325g(40p)", "Xtra.det.kingsize 300g(40p)");
                products.put("FLU ECLAIRS POUCH 1/-", "F ECLAIRS POUCH 1/-");
                products.put("sugarfree butter 75*", "sugarfree butter 75g*");
                products.put("sugarfree oats 75*", "sugarfree oats 75g*");
                products.put("sugarfree cashew75*", "sugarfree cashew75g*");
                products.put("ENEGA bodylotion(24p)", "ENEGA bodylotion 20ml(24p)");
                products.put("B fizz", "B fizz 160 ml");
                products.put("Aachi Garam masala 10/-(1000p)", "Aachi Garam masala 10/-(500p)");
                products.put("BUTTERLY 12x12x35g", "BUTTERLY 12x16x35g");
                products.put("Aachi chilly powder 1kg", "Aachi chilly powder 50gms");
                products.put("Chips C&N (15stx14p)", "Chips C&N (16st x 14p)");
                products.put("Parle-G 75/- (16p)", "Parle-G 75/- (14p)");
                products.put("Chips TT (15stx14p)", "Chips TT (16st x14p)");
                products.put("A CHICKEN  \\10/-(50P)", "A CHICKEN 10/-(50P)");
                products.put("Chips C&S (15stx14p)", "Chips C&S (16st x 14p)");


                if(products.containsKey(jpaSoldStock.getProductName())){
                    ProductDetails productDetails2 = productRepository.findByPname(products.get(jpaSoldStock.getProductName().trim()));
                    billingDetails.setProduct_name(productDetails2.getId());
                }
                else {
                    Scanner scanner = new Scanner(System.in);
                    JpaSoldStock jpaSoldStock1 = jpaSoldStock;
                    jpaSoldStock1.setAmount(jpaSoldStock.getAmount() / jpaSoldStock1.getQty());
//                System.out.println("product bill details"+jpaSoldStock1.toString());
                    printProductTable(jpaSoldStock);
                    System.out.print("Product " + jpaSoldStock.getProductName() + " got renamed Please enter Current name");
                    String productName = scanner.nextLine();
                    ProductDetails productDetails1 = productRepository.findByPname(productName);
                    billingDetails.setProduct_name(productDetails1.getId());
                }
            }else {
                billingDetails.setProduct_name(productDetails.getId());
            }
            billingDetails.setAmount(jpaSoldStock.getAmount()/jpaSoldStock.getQty());
            billingDetails.setDisc(jpaSoldStock.getDisc());
            billingDetails.setDgst(dgst);
            billingDetails.setQty(jpaSoldStock.getQty());
            billingDetails.setFree(jpaSoldStock.getFree().toString());
            billingDetails.setHsn_code(jpaSoldStock.getHsnCode());
            billingDetails.setProduct_gst(jpaSoldStock.getProductGst());
            billingDetails.setUnites_per(jpaSoldStock.getUnitesPer());
            billingDetails.setMrp(jpaSoldStock.getMrp());
            billingDetails.setProduct_company(jpaSoldStock.getProductCompany());
            billingDetails.setAmount_after_disc(jpaSoldStock.getAmountAfterDisc());
            billingDetails.setBno(jpaSoldStock.getBno());
            billItemsRepository.save(billingDetails);
              }


    }

    public void copyCompanyDetails(String dgst){
//        List<JpaCompanyGstList> jpaCompanyGstLists= jpaCompanyRepo.findCompanyGstGroupedByCname();
        List<Object[]> results = jpaCompanyRepo.findCompanyGstGroupedByCname();

        List<JpaCompanyGstList> companies = new ArrayList<>();
        for (Object[] result : results) {
            Integer cid = (Integer) result[0];
            String cname = (String) result[1];
            List<Integer> gstList = Arrays.stream(((String) result[2]).split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            companies.add(new JpaCompanyGstList(cid, cname, gstList));
        }

//        return companies;

        for (JpaCompanyGstList jpaCompanyGstList : companies
        ){
            CompanyDetails companyDetails = new CompanyDetails();
            companyDetails.setDgst(dgst);
            companyDetails.setName(jpaCompanyGstList.getCname());
            companyDetails.setGstPercentage(jpaCompanyGstList.getGstList());
            companyRepository.save(companyDetails);
        }
    }

    public  void printProductTable(JpaSoldStock jpaSoldStock) {

        // Print the table header
        System.out.printf("%-5s %-5s %-15s %-5s %-5s %-10s %-10s %-5s %-10s %-10s %-10s %-15s %-5s %-15s %-5s%n",
                "Cno", "Bno", "Product Name", "GST", "Qty", "Amount", "Billing Date", "Free", "HSN Code", "Units/Per", "MRP", "Product Company", "Disc", "Amount After Disc", "ID");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        // Print each product
//        for (Product product : products) {
            System.out.printf("%-5s %-5s %-15s %-5s %-5s %-10s %-10s %-5s %-10s %-10s %-10s %-15s %-5s %-15s %-5s%n",
                    jpaSoldStock.getCno(), jpaSoldStock.getBno(), jpaSoldStock.getProductName(), jpaSoldStock.getProductGst(), jpaSoldStock.getQty(),
                    jpaSoldStock.getAmount(), jpaSoldStock.getBillingDate(), jpaSoldStock.getFree(), jpaSoldStock.getHsnCode(),
                    jpaSoldStock.getUnitesPer(), jpaSoldStock.getMrp(), jpaSoldStock.getProductCompany(), jpaSoldStock.getDisc(),
                    jpaSoldStock.getAmountAfterDisc(), jpaSoldStock.getId());
//        }
    }

    public void copyBillsAmount(String dgst){
        List<JpaBillsAmount> jpaBillsAmounts = jpaBillsAmountRepo.findAll();
        for (JpaBillsAmount jpaBillsAmount:jpaBillsAmounts
             ) {
            BillAmountDetails billAmountDetails = new BillAmountDetails();
            Customer customer = customerRepository.findByCno(jpaBillsAmount.getCno().toString());
            billAmountDetails.setCno(customer.getId());
            billAmountDetails.setAmount(jpaBillsAmount.getAmount());
            billAmountDetails.setDate(jpaBillsAmount.getDate().toString());
            billAmountDetails.setBno(jpaBillsAmount.getBno());
            billAmountDetails.setDgst(dgst);
            billAmountRepository.save(billAmountDetails);

        }
    }

   public void copyCustomerToBalanceDetails(String dgst){
        List<Customer> customerList = customerRepository.findAll();
       for (Customer customer:customerList
            ) {
           BalanceDetails balanceDetails = new BalanceDetails();
           balanceDetails.setCno(customer.getId());
           balanceDetails.setCname(customer.getCname());
           balanceDetails.setTotalBalance(0.0);
           balanceDetails.setDebitBalance(0.0);
           balanceDetails.setCreditBalance(0.0);
           balanceDetails.setDgst(dgst);
           balanceDetailsRepository.save(balanceDetails);

       }


   }

   public List<BalanceDetails> getBalanceDetails(String dgst){
        return balanceDetailsRepository.findByDgst(dgst);
   }

   public void modifyBalanceDetails(BalanceDetails balanceDetails){

       LocalDate date = LocalDate.now();
       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
       String updatedDate = date.format(formatter);
       System.out.println("updated "+updatedDate);

        BalanceDetails existingBalanceDetails = balanceDetailsRepository.findByCno(balanceDetails.getCno());
        existingBalanceDetails.setLastUpdatedDate(updatedDate);
        existingBalanceDetails.setReason("Manually Updated");
        if(balanceDetails.getDebitBalance()!=0){
            existingBalanceDetails.setType("Debit");
            Double balance=existingBalanceDetails.getTotalBalance()-balanceDetails.getDebitBalance();
            existingBalanceDetails.setLastUpdateAmount(balanceDetails.getDebitBalance());
            existingBalanceDetails.setTotalBalance(balance);
        }else if(balanceDetails.getCreditBalance()!=0) {
            existingBalanceDetails.setType("Credit");
           Double balance=existingBalanceDetails.getTotalBalance()+balanceDetails.getCreditBalance();
            existingBalanceDetails.setLastUpdateAmount(balanceDetails.getCreditBalance());
            existingBalanceDetails.setTotalBalance(balance);
       }
        balanceDetailsRepository.save(existingBalanceDetails);

   }

   public BalanceDetails getBalanceDetailsById(String id){
        return balanceDetailsRepository.findByCno(id);
   }

    public static List<WayBillDetails> calculateWayBillDetails(List<BillingDetails> billingItemDetailsList) {
        // Group BillingItemDetails by hsn_code and calculate sums
        Map<String, Double> gstAmountSumMap = billingItemDetailsList.stream()
                .collect(Collectors.groupingBy(BillingDetails::getHsn_code,
                        Collectors.summingDouble(BillingDetails::getGstamount)));

        Map<String, Double> amountSumMap = billingItemDetailsList.stream()
                .collect(Collectors.groupingBy(BillingDetails::getHsn_code,
                        Collectors.summingDouble(BillingDetails::getAmount)));

        Map<String, Integer> productGstSumMap = billingItemDetailsList.stream()
                .collect(Collectors.toMap(BillingDetails::getHsn_code,
                        BillingDetails::getProduct_gst,
                        (existing, replacement) -> existing));

        // Create WayBillDetails objects using the calculated sums
        return gstAmountSumMap.entrySet().stream()
                .map(entry -> {
                    WayBillDetails wayBillDetails = new WayBillDetails();
                    wayBillDetails.setHsn_code(entry.getKey());
                    wayBillDetails.setGstAmountSum(entry.getValue());
                    wayBillDetails.setAmountSum(amountSumMap.getOrDefault(entry.getKey(), 0.0));
                    wayBillDetails.setProduct_gst(productGstSumMap.getOrDefault(entry.getKey(), 0));
                    return wayBillDetails;
                })
                .collect(Collectors.toList());
    }

    public  List<SalesPerGST> getGstSalesOfGstCustomers(String fromDate, String toDate){
        List<String> cnoOfGstCustomers= customerRepository.findGstCustomerIdsWithGst();
        List<SalesPerGST> salesPerGSTS = billingRepositry.getGstSales(cnoOfGstCustomers,fromDate,toDate);
        return  salesPerGSTS;
    }

    public  List<SalesPerGST> getGstSalesOfCustomers(String fromDate, String toDate){

        List<String> cnoOfGstCustomers= customerRepository.findCustomerIds();
        List<SalesPerGST> salesPerGSTS = billingRepositry.getGstSales(cnoOfGstCustomers,fromDate,toDate);
        return  salesPerGSTS;
    }

    public void addCessandNetAmount(String date) throws ParseException {
        List<BillingDetails> billingDetailsList = new ArrayList<>();
        billingDetailsList=billItemsRepository.findAll();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate= formatter.parse(date);
        for (BillingDetails billingDetails:billingDetailsList
             ) {
               Date billingDate= formatter.parse(billingDetails.getBilling_date());
               if(billingDate.after(startDate)){
                   ProductDetails productDetails = productRepository.findById(billingDetails.getProduct_name()).orElse(null);
                   float per =productDetails.getVatp()+productDetails.getCess();
                   Double netAmount = (billingDetails.getAmount_after_disc()/((per)+100))*100;
                   Double cessAmount= percentageUtils.getPercentageAmount(netAmount,productDetails.getCess());
                   billingDetails.setNetAmount(netAmount);
                   billingDetails.setCessAmount(cessAmount);
                   billingDetails.setCess(productDetails.getCess());
                   billItemsRepository.save(billingDetails);
               }
               else{
                   Double netAmount = (billingDetails.getAmount_after_disc()/((billingDetails.getProduct_gst())+100))*100;
                   billingDetails.setNetAmount(netAmount);
                   billingDetails.setCessAmount(0.00);
                   billingDetails.setCess(0);
                   billItemsRepository.save(billingDetails);
               }
        }



    }
    public List<MonthlySales> getSixMonthsSale() {
        List<MonthlySales> monthlySales = billingRepositry.getSumOfAmountAfterDiscForLastSixMonths();

        // Month number to name map
        Map<Integer, String> monthMap = new HashMap<>();
        monthMap.put(1, "January");
        monthMap.put(2, "February");
        monthMap.put(3, "March");
        monthMap.put(4, "April");
        monthMap.put(5, "May");
        monthMap.put(6, "June");
        monthMap.put(7, "July");
        monthMap.put(8, "August");
        monthMap.put(9, "September");
        monthMap.put(10, "October");
        monthMap.put(11, "November");
        monthMap.put(12, "December");

        List<MonthlySales> transformedAndSortedSales = monthlySales.stream()
                .map(sales -> {
                    String id = sales.getId();
                    String newId = id; // Initialize newId with the original id

                    try {
                        // Parse the year and month from the id
                        String[] parts = id.split("-");
                        if (parts.length == 2) {
                            int year = Integer.parseInt(parts[0]);
                            int monthNumber = Integer.parseInt(parts[1]);

                            if (monthNumber >= 1 && monthNumber <= 12) {
                                String month = monthMap.get(monthNumber);
                                newId = month + " " + year; // Format newId as "Month Year"
                            }
                        }
                    } catch (NumberFormatException e) {
                        // Log or handle the exception with more context
                        System.err.println("Invalid ID format for sales: " + sales);
                    }

                    // Create a new MonthlySales object with the updated id
                    return new MonthlySales(newId, sales.getTotalAmount()); // Adjust constructor if needed
                })
                .sorted(Comparator.comparing(sales -> {
                    String[] parts = sales.getId().split(" ");
                    int year = 0;
                    int monthNumber = 0;

                    if (parts.length == 2) {
                        String monthName = parts[0];
                        year = Integer.parseInt(parts[1]);
                        monthNumber = monthMap.entrySet()
                                .stream()
                                .filter(entry -> entry.getValue().equals(monthName))
                                .map(Map.Entry::getKey)
                                .findFirst()
                                .orElse(0);
                    }
                    return LocalDate.of(year, monthNumber, 1);
                }))
                .collect(Collectors.toList());

        return transformedAndSortedSales;
    }

    public List<MonthlySales> getSixMonthsSaleForCompanies(String company,int noOfMonths) {
        List<MonthlySales> monthlySales = billingRepositry.getSumOfAmountAfterDiscForLastSixMonthsPerCompany(company,noOfMonths);

        // Month number to name map
        Map<Integer, String> monthMap = new HashMap<>();
        monthMap.put(1, "January");
        monthMap.put(2, "February");
        monthMap.put(3, "March");
        monthMap.put(4, "April");
        monthMap.put(5, "May");
        monthMap.put(6, "June");
        monthMap.put(7, "July");
        monthMap.put(8, "August");
        monthMap.put(9, "September");
        monthMap.put(10, "October");
        monthMap.put(11, "November");
        monthMap.put(12, "December");

        List<MonthlySales> transformedAndSortedSales = monthlySales.stream()
                .map(sales -> {
                    String id = sales.getId();
                    String newId = id; // Initialize newId with the original id

                    try {
                        // Parse the year and month from the id
                        String[] parts = id.split("-");
                        if (parts.length == 2) {
                            int year = Integer.parseInt(parts[0]);
                            int monthNumber = Integer.parseInt(parts[1]);

                            if (monthNumber >= 1 && monthNumber <= 12) {
                                String month = monthMap.get(monthNumber);
                                newId = month + " " + year; // Format newId as "Month Year"
                            }
                        }
                    } catch (NumberFormatException e) {
                        // Log or handle the exception with more context
                        System.err.println("Invalid ID format for sales: " + sales);
                    }

                    // Create a new MonthlySales object with the updated id
                    return new MonthlySales(newId, sales.getTotalAmount()); // Adjust constructor if needed
                })
                .sorted(Comparator.comparing(sales -> {
                    String[] parts = sales.getId().split(" ");
                    int year = 0;
                    int monthNumber = 0;

                    if (parts.length == 2) {
                        String monthName = parts[0];
                        year = Integer.parseInt(parts[1]);
                        monthNumber = monthMap.entrySet()
                                .stream()
                                .filter(entry -> entry.getValue().equals(monthName))
                                .map(Map.Entry::getKey)
                                .findFirst()
                                .orElse(0);
                    }
                    return LocalDate.of(year, monthNumber, 1);
                }))
                .collect(Collectors.toList());

        return transformedAndSortedSales;
    }
    public void setLegalName(){

        List<Customer> customerList = customerRepository.findAll();
        for (Customer customer : customerList
        ) {
            System.out.println("GstNo: "+customer.getCtno());
            if(customer.getCtno() != "not avaliable" && !customer.getCtno().equals(null)) {
                GstDetailsResponse gstDetailsResponse = apiServices.getGstinData(customer.getCtno());
                System.out.println("response" + gstDetailsResponse.getData().getTradeNam());
                customer.setLegal_name(gstDetailsResponse.getData().getLgnm());
                System.out.println("legal name"+gstDetailsResponse.getData().getLgnm());
                customerRepository.save(customer);
            }


        }


    }

    public Set<String> getHsncodesNotAvaliable(){
        List<String> hsncodes= new ArrayList<>();
        List<ProductDetails> productDetails = productRepository.findAll();
        List<UqcAndDescription> uqcAndDescriptions = uqcAndDescriptionRepository.findAll();
        Set<String> allHsnCodes = uqcAndDescriptions.stream()
                .map(UqcAndDescription::getHsnCode)
                .collect(Collectors.toSet());

        Set<String> hsncodeNotPresent = productDetails.stream()
                .map(ProductDetails::getHsn_code)
                .filter(hsnCode -> !allHsnCodes.contains(hsnCode))
                .collect(Collectors.toSet());

       return hsncodeNotPresent;


    }

    public List<UqcAndDescription> getAllHsncodeDetails(){
        return uqcAndDescriptionRepository.findAll();
    }

    public void addHsnCodeDetails(UqcAndDescription uqcAndDescription){
        uqcAndDescriptionRepository.save(uqcAndDescription);
    }

    public void deActivateCompany(String companyId){
        CompanyDetails companyDetails = companyRepository.findById(companyId).orElse(null);
        companyDetails.setStatus("D");
        companyRepository.save(companyDetails);
    }

    public void deActivateProduct(String productId){
        ProductDetails productDetails = productRepository.findById(productId).orElse(null);
        productDetails.setStatus("D");
        productRepository.save(productDetails);
    }

    public void deActivateCustomer(String customerId){
        Customer customer = customerRepository.findById(customerId).orElse(null);
        customer.setStatus("D");
        customerRepository.save(customer);
    }
}