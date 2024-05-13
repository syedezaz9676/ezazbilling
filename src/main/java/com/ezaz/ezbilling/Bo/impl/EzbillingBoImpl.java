package com.ezaz.ezbilling.Bo.impl;

import com.ezaz.ezbilling.Bo.EzbillingBo;
import com.ezaz.ezbilling.Util.PercentageUtils;
import com.ezaz.ezbilling.model.*;
import com.ezaz.ezbilling.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

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

    @Autowired
    private MongoTemplate mongoTemplate;

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

    public EzbillingBoImpl(CustomerRepository customerRepository, GstCodeDetailsRepo gstCodeDetailsRepo, ChangeDateFormatRepo changeDateFormatRepo, BillAmountRepository billAmountRepository, CompanyRepository companyRepository, ProductRepository productRepository, ProductDetailsRepository productDetailsRepository, CompanyDetailsRepository companyDetailsRepository, CustomerRepositoryCustom customerRepositoryCustom, UsersRepository usersRepository, BillingRepositry billingRepositry, BillItemsRepository billItemsRepository, PercentageUtils percentageUtils, StockItemDetails stockItemDetails, StockRepository stockRepository) {
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
    public String saveBillItems(List<BillingDetails> billingDetailsList) throws ParseException {
        Double totalBillAmount=0.0;

          BillingDetails firstBillItem = billingDetailsList.get(0);

          BillAmountDetails billAmountDetails= new BillAmountDetails();
          billAmountDetails.setCno(firstBillItem.getCno());
          billAmountDetails.setDgst(firstBillItem.getDgst());
         final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
          User user= usersRepository.findById(firstBillItem.getDgst()).orElse(null);

          BillingDetails billingDetailsforMaxBillno = billingRepositry.getMaxBillNo(user.getId());
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

               billingDetails.setBilling_date(billingDetails.getBilling_date().substring(0,10));
               billingDetails.setBno(newInvoiceNo);
               billingDetails.setAmount_after_disc((billingDetails.getAmount()*billingDetails.getQty()));
               billItemsRepository.save(billingDetails);
               totalBillAmount = totalBillAmount+billingDetails.getAmount()*billingDetails.getQty();
        }
            billAmountDetails.setAmount(totalBillAmount);

            billAmountRepository.save(billAmountDetails);


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
            billingDetails.setCess(productDetails.getCess());
            Double cessAmount= percentageUtils.getPercentageAmount(billingDetails.getAmount(),productDetails.getCess());
            Double gst_amount =percentageUtils.getPercentageAmount(billingDetails.getAmount(),productDetails.getVatp());
            Double actualCostBeforeGstAndDiscount = billingDetails.getAmount()-(cessAmount+gst_amount);
            Double actualAmountAfterDisc= actualCostBeforeGstAndDiscount - percentageUtils.getPercentageAmount(actualCostBeforeGstAndDiscount,billingDetails.getDisc());
            Double cessAmountAfterDisc= percentageUtils.getPercentageAmount(actualAmountAfterDisc,productDetails.getCess())*billingDetails.getQty();
            Double gstAmountAfterDisc=percentageUtils.getPercentageAmount(actualAmountAfterDisc,productDetails.getVatp())*billingDetails.getQty();
            Double totalGrossAmount = actualAmountAfterDisc*billingDetails.getQty();
            billingDetails.setCessAmount(cessAmount*billingDetails.getQty());
            billingDetails.setGstamount(gst_amount*billingDetails.getQty());
            billingDetails.setGross_amount(actualCostBeforeGstAndDiscount*billingDetails.getQty());
            billingDetails.setRate(billingDetails.getAmount());
            Double totalamountafterdiscount= billingDetails.getAmount()-percentageUtils.getPercentageAmount(billingDetails.getAmount(),billingDetails.getDisc())-cessAmount;
            Double gross_amount=totalamountafterdiscount - percentageUtils.getPercentageAmount(totalamountafterdiscount,billingDetails.getProduct_gst());
            billingDetails.setTotal_amount(cessAmountAfterDisc+gstAmountAfterDisc+actualCostBeforeGstAndDiscount);
//            Float gst_amount =billingDetails.getAmount()-gross_amount;

//            billingDetails.setGross_amount(gross_amount*billingDetails.getQty());
            billingDetails.setAmount(billingDetails.getAmount()*billingDetails.getQty());

            billingDetails.setAmount_after_disc(billingDetails.getAmount()*billingDetails.getQty());
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
                billingDetails.setAmount_after_disc((billingDetails.getAmount() * billingDetails.getQty()) - percentageUtils.getPercentageAmount(billingDetails.getAmount() * billingDetails.getQty(), billingDetails.getDisc()));
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
            GstReport gstReport = new GstReport();
            gstReport.setCustomerName(customerDetailswithGstNo.getCname());
            gstReport.setGstNo(customerDetailswithGstNo.getCtno());

            List<BillNo> billnos = billItemsRepository.findBnoByCnoAndBillingDateBetween(customerDetailswithGstNo.getId(), startDate, endDate);

            Map<String, BillGstDetails> billGstDetailsMap = new HashMap<>();
            System.out.println("billnos.size"+billnos.size());
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
                    sumOfGst.setSumOfGstAmount(billAggregationResult.getTotalAmount()-(billAggregationResult.getTotalAmount()*billAggregationResult.getProduct_gst()/ 100.0));
                    sumOfGst.setBno(billAggregationResult.getBno());
                    sumOfGst.setBillingDate(billAggregationResult.getBillingDate());

                    // Calculate taxable amount by subtracting GST amount from total amount
                    Double taxableAmount = billAggregationResult.getTotalAmount()-(billAggregationResult.getTotalAmount()*billAggregationResult.getProduct_gst()/ 100.0);

                    // Check if the current GST value has already been added for this BillGstDetails
                    if (!addedGstValues.contains(sumOfGst.getGst())) {
                        addedGstValues.add(sumOfGst.getGst());

                        billGstDetails.getSumOfGsts().add(sumOfGst);
                        totalSum += billAggregationResult.getTotalAmount(); // Calculate total sum of GST amounts
                        totalTaxableSum += taxableAmount; // Calculate total taxable sum
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

        return gstReports;
    }


    public List<SoldStockSummary> getGstDetailsForHsnCode(String startDate, String endDate){

        List<SoldStockSummary> soldStockSummaries =billingRepositry.getSoldStockSummary(startDate,endDate);
        List<SoldStockSummary> soldStockSummariesWithIgst =billingRepositry.getSoldStockSummaryForHsncode(startDate,endDate);

        List<SoldStockSummary> finalSoldStockSummaries = Stream.concat(soldStockSummaries.stream(), soldStockSummariesWithIgst.stream())
                .collect(Collectors.toList());

        return finalSoldStockSummaries;

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
                            .set("state", user1.getState()),
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
            System.out.println("convertedDate"+convertedDate);
        if(convertedDate!=null)
        billingDetails.setBilling_date(convertedDate);

            changeDateFormatRepo.save(billingDetails);


        }
    }

    public static String convertToISO8601(String dateString) {
        System.out.println("dataString "+dateString);
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
           System.out.println("amountDetails.getCno()"+amountDetails.getCno());
           amountDetails.setCno(customer.getCname());
           billAmountDetailsWithCustomerNames.add(amountDetails);
       }
       return billAmountDetailsWithCustomerNames;
   }




}