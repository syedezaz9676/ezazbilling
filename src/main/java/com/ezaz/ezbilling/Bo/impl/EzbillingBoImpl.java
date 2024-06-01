package com.ezaz.ezbilling.Bo.impl;

import com.ezaz.ezbilling.Bo.EzbillingBo;
import com.ezaz.ezbilling.Util.MongodbBackup;
import com.ezaz.ezbilling.Util.PercentageUtils;
import com.ezaz.ezbilling.model.*;
import com.ezaz.ezbilling.model.mysql.*;
import com.ezaz.ezbilling.repository.*;
import com.ezaz.ezbilling.repository.mysql.*;
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

   @Autowired
   private MongodbBackup mongodbBackup;

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

    public EzbillingBoImpl( CustomerRepository customerRepository, GstCodeDetailsRepo gstCodeDetailsRepo, ChangeDateFormatRepo changeDateFormatRepo, BillAmountRepository billAmountRepository, CompanyRepository companyRepository, ProductRepository productRepository, ProductDetailsRepository productDetailsRepository, CompanyDetailsRepository companyDetailsRepository, CustomerRepositoryCustom customerRepositoryCustom, UsersRepository usersRepository, BillingRepositry billingRepositry, BillItemsRepository billItemsRepository, PercentageUtils percentageUtils, StockItemDetails stockItemDetails, StockRepository stockRepository, JpaCustomerRepo jpaCustomerRepo, JpaProductRepo jpaProductRepo, JpaSoldStockRepo jpaSoldStockRepo, JpaCompanyRepo jpaCompanyRepo, JpaBillsAmountRepo jpaBillsAmountRepo, BalanceDetailsRepository balanceDetailsRepository) {

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
        Customer customer1 = customerRepository.findByCname(customer.getCname());
        if(customer.getIsEdit() == false) {
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


        System.out.println("product details"+productDetails.toString());
        productRepository.save(productDetails);
        ProductDetails productDetails1 = productRepository.findByPname(productDetails.getPname());
        StockDetails stockDetails = new StockDetails();
        stockDetails.setPid(productDetails1.getId());
        stockDetails.setPname(productDetails1.getPname());
        stockDetails.setIn_stock_units(0.0);
        stockDetails.setDgst(productDetails.getDgst());
        stockDetails.setPcom(productDetails.getPcom());
        stockItemDetails.save(stockDetails);


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
        BalanceDetails balanceDetails = balanceDetailsRepository.findByCno(firstBillItem.getCno());
        balanceDetails.setReason("invoice added");
        balanceDetails.setType("Credit");
        Double balanceAmount=balanceDetails.getTotalBalance()+totalBillAmount;
        balanceDetails.setTotalBalance(balanceAmount);
        balanceDetails.setLastUpdatedDate(firstBillItem.getBilling_date());
        balanceDetails.setLastUpdateAmount(totalBillAmount);
        balanceDetailsRepository.save(balanceDetails);
        mongodbBackup.BackUp();

        return newInvoiceNo;
    }

    @Override
    public SavedBillandWayBillDetails getSavedBillDetailsByinvoiceNo(String invoiceNo) {

          SavedBillandWayBillDetails savedBillandWayBillDetails = new SavedBillandWayBillDetails();
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
            System.out.println("productDetails.getPname()"+productDetails.getPname());
            Double rateAfter = (billingDetails.getAmount()/(productDetails.getVatp()+100))*100;

//            Double cessAmount= percentageUtils.getPercentageAmount(billingDetails.getAmount(),productDetails.getCess());
//            Double gst_amount =percentageUtils.getPercentageAmount(billingDetails.getAmount(),productDetails.getVatp());
            Double cessAmount= percentageUtils.getPercentageAmount(rateAfter,productDetails.getCess());
            Double gst_amount =percentageUtils.getPercentageAmount(rateAfter,productDetails.getVatp());
            System.out.println("gst_amount"+gst_amount*billingDetails.getQty());
//            System.out.println("gst_amount"+percentageUtils.getPercentageAmount(billingDetails.getAmount()*billingDetails.getQty(),productDetails.getVatp()));
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
        List<WayBillDetails> wayBillDetails = calculateWayBillDetails(savedBillDetails);
        System.out.println("waybill"+wayBillDetails);

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
                billingDetails.setAmount_after_disc(billingDetails.getAmount() * billingDetails.getQty());
                billItemsRepository.save(billingDetails);
                totalAmount=totalAmount+billingDetails.getAmount() * billingDetails.getQty();
            }
            BillAmountDetails billAmountDetails = billAmountRepository.findByBno(firstBillItem.getBno());
            BalanceDetails existingBalanceDetails = balanceDetailsRepository.findByCno(firstBillItem.getCno());
            existingBalanceDetails.setReason("Invoice Updated");
            Double existingBalance= existingBalanceDetails.getTotalBalance();
            if(billAmountDetails.getAmount()>totalAmount){
                existingBalanceDetails.setType("Debit");
                Double differenceAmount= billAmountDetails.getAmount()-totalAmount;
                billAmountDetails.setAmount(billAmountDetails.getAmount()-differenceAmount);
                billAmountRepository.save(billAmountDetails);
                existingBalanceDetails.setTotalBalance(existingBalance-differenceAmount);
                existingBalanceDetails.setLastUpdatedDate(firstBillItem.getBilling_date());
                existingBalanceDetails.setLastUpdateAmount(differenceAmount);
                balanceDetailsRepository.save(existingBalanceDetails);
            }else if(billAmountDetails.getAmount()<totalAmount){
                existingBalanceDetails.setType("Credit");
                Double differenceAmount= totalAmount-billAmountDetails.getAmount();
                billAmountDetails.setAmount(billAmountDetails.getAmount()+differenceAmount);
                billAmountRepository.save(billAmountDetails);
                existingBalanceDetails.setTotalBalance(existingBalance+differenceAmount);
                existingBalanceDetails.setLastUpdatedDate(firstBillItem.getBilling_date());
                existingBalanceDetails.setLastUpdateAmount(differenceAmount);
                balanceDetailsRepository.save(existingBalanceDetails);
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
                    sumOfGst.setSumOfGstAmount(billAggregationResult.getTotalAmount()-((billAggregationResult.getTotalAmount()/(billAggregationResult.getProduct_gst()+100))*100.0));
                    sumOfGst.setBno(billAggregationResult.getBno());
                    sumOfGst.setBillingDate(billAggregationResult.getBillingDate());

                    // Calculate taxable amount by subtracting GST amount from total amount
                    Double taxableAmount = (billAggregationResult.getTotalAmount()-(billAggregationResult.getTotalAmount()/(billAggregationResult.getProduct_gst()+100))*100.0);

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
            System.out.println("cno"+amountDetails.getCno());
        Customer customer = customerRepository.findByCno(amountDetails.getCno());
            System.out.println("cusotmer"+customer.getDgst());
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
            System.out.println("product name"+jpaSoldStock.getProductName());
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
        System.out.println("cno size"+cnoOfGstCustomers.size());
        List<SalesPerGST> salesPerGSTS = billingRepositry.getGstSales(cnoOfGstCustomers,fromDate,toDate);
        return  salesPerGSTS;
    }

    public  List<SalesPerGST> getGstSalesOfCustomers(String fromDate, String toDate){

        List<String> cnoOfGstCustomers= customerRepository.findCustomerIds();
        System.out.println("cno size"+cnoOfGstCustomers.size());
        List<SalesPerGST> salesPerGSTS = billingRepositry.getGstSales(cnoOfGstCustomers,fromDate,toDate);
        return  salesPerGSTS;
    }
}