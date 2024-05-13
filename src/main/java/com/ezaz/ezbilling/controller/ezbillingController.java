package com.ezaz.ezbilling.controller;

import com.ezaz.ezbilling.Bo.EzbillingBo;
import com.ezaz.ezbilling.model.*;
import com.ezaz.ezbilling.repository.BillingRepositry;
import com.ezaz.ezbilling.repository.CompanyRepository;
import com.ezaz.ezbilling.repository.StockItemDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8081/")
@RestController
public class ezbillingController {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private BillingRepositry billingRepositry;

    @Autowired
    private EzbillingBo ezbillingBo;

    @CrossOrigin(origins = "http://localhost:8081/")
    @PostMapping("/savecompanydetails")
    public String saveCompany(@RequestBody CompanyDetails companyDetails){
        ezbillingBo.saveCompanyDetails(companyDetails);
        return "save success";
    }
    @GetMapping("/welcome")
    public String welcome(){
        return "welcome";
    }
    @PostMapping("/savecustomerdetails")
    public ResponseEntity<?> saveCustomer(@RequestBody Customer customer){
        ezbillingBo.saveCustomerToDB(customer);
        String success= "sucess";
        return ResponseEntity.ok(success);
    }

//    @CrossOrigin
//    @GetMapping("/getgstcodedetails")
    @RequestMapping(value="/getgstcodedetails", method = RequestMethod.GET)
    public List<GstCodeDetails> getGstCodeDetails(){
        return ezbillingBo.getGstCodeDetails();
    }


    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/getcompanydetails/{id}", method = RequestMethod.GET)
    public List<CompanyDetails> getCompanysDetails(@PathVariable String id){
        return ezbillingBo.getCompanyDetails(id);
    }

    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/getproductdetails/{id}", method = RequestMethod.GET)
    public List<ProductDetails> getProductDetails(@PathVariable String id){
        return ezbillingBo.getProductsDetails(id);
    }

    @CrossOrigin(origins = "http://localhost:8081/")
    @PostMapping("/saveproductdetails")
    public String saveProductDetails(@RequestBody ProductDetails productDetails){
        ezbillingBo.saveProductDetails(productDetails);
        String success= "sucess";
        return success;
    }
    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/getproductnames/{id}", method = RequestMethod.GET)
    public List<ProductNames> getProductNames(@PathVariable String id){
        return ezbillingBo.getProductNames(id);
    }

    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/getproductdetailsbyid/{id}", method = RequestMethod.GET)
    public ResponseEntity<ProductDetails> getProductById(@PathVariable String id) {
        Optional<ProductDetails> product = ezbillingBo.getProductDetailsByID(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/getcustomernames/{id}", method = RequestMethod.GET)
    public List<CustomerNames> getCustomerNames(@PathVariable String id){
        return ezbillingBo.getCustomerNames(id);
    }
    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/getcompanynames/{id}", method = RequestMethod.GET)
    public List<CompanyNames> getCompanyNames(@PathVariable String id){
        return ezbillingBo.getCompanyNames(id);
    }
    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/getcustomerdetailsbyid/{id}", method = RequestMethod.GET)
    public ResponseEntity<Customer> getcutomerById(@PathVariable String id) {
        Optional<Customer> customer = ezbillingBo.getCustomerDetailsByID(id);
        return customer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/getcompanydetailsbyid/{id}", method = RequestMethod.GET)
    public ResponseEntity<CompanyDetails> getcompanyById(@PathVariable String id) {
        Optional<CompanyDetails> companydetails = ezbillingBo.getCompanyDetailsByID(id);
        return companydetails.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/getproductsbycompany/{companyname}", method = RequestMethod.GET)
    public List<ProductNames> getproductsByCompany(@PathVariable String companyname) {
        List<ProductNames> productsByCompany = ezbillingBo.getProductDetailsByCompany(companyname);
        return productsByCompany;
    }
    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/getcustomerdetailsbydgst/{id}", method = RequestMethod.GET)
    public List<Customer> getcustomerDetaisByDgst(@PathVariable String id) {
        List<Customer> customerByDgst = ezbillingBo.getCustomerByDgst(id);
        return customerByDgst;
    }
    @CrossOrigin(origins = "http://localhost:8081/")
    @PostMapping("/savebillingdetails")
    public ResponseEntity<?> savebillingdetails(@RequestBody List<BillingDetails> billDetails) throws ParseException {
        String Invoice= ezbillingBo.saveBillItems(billDetails) ;
        return ResponseEntity.ok(Invoice);
    }

    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/getbillDetailsbyinvoiceno/{invoiceNo}", method = RequestMethod.GET)
    public ResponseEntity<?> getbillDetailsbyinvoiceno (@PathVariable String invoiceNo){
        List<BillingDetails> savedBillDetails= ezbillingBo.getSavedBillDetailsByinvoiceNo(invoiceNo);
        return ResponseEntity.ok(savedBillDetails);
    }

    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/getinvoicebyinvoiceno/{invoiceNo}", method = RequestMethod.GET)
    public ResponseEntity<?> getinvoiceDetailsbyinvoiceno (@PathVariable String invoiceNo){
        BillDetails savedBillDetails= ezbillingBo.getBillDetailsByInvoiceNo(invoiceNo);
        return ResponseEntity.ok(savedBillDetails);
    }

    @CrossOrigin(origins = "http://localhost:8081/")
    @PostMapping("/updatebillingdetails")
    public ResponseEntity<?> updatebillingdetails(@RequestBody List<BillingDetails> billDetails){
        String Invoice= ezbillingBo.updateBillItems(billDetails); ;
        return ResponseEntity.ok(Invoice);
    }
    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/getgstdetails", method = RequestMethod.GET)
    public List<GstReport> getGstDetailsOfCustomer (@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) throws ParseException {
     return ezbillingBo.getGstDetailsByDate(startDate,endDate);

    }

    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/gethsngstdetails", method = RequestMethod.GET)
    public List<SoldStockSummary> getGstDetailsForHsnCode (@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) throws ParseException {
        return ezbillingBo.getGstDetailsForHsnCode(startDate,endDate);

    }

    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/getstockdetailsbyid/{id}", method = RequestMethod.GET)
    public StockDetails getStockDetailsbyId (@PathVariable String id) {
        return ezbillingBo.getStockItemDetails(id);

    }

    @CrossOrigin(origins = "http://localhost:8081/")
    @PostMapping("/savestockdetails")
    public String saveStockDetails(@RequestBody StockDetails stockDetails){
        ezbillingBo.saveStockDetails(stockDetails);
        return "saved stock details";
    }

    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/copyproductstostock/{dgst}", method = RequestMethod.GET)
    public String copyProductsToStock (@PathVariable String dgst) {
        ezbillingBo.copyProductToStock(dgst);
        return "copy done";

    }
    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/getstockbypcomanddgst/{dgst}", method = RequestMethod.GET)
    public List<StockDetails> getStockByPcomAndDgst (@PathVariable String dgst) {
         return ezbillingBo.getStockDetailsByDgst(dgst);

    }

    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/getsalesdetails", method = RequestMethod.GET)
    public List<CompanyBillingSummary> getSalesDetails (@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) throws ParseException {
        return ezbillingBo.getSalesDetails(startDate,endDate);

    }

    @CrossOrigin(origins = "http://localhost:8081/")
    @PostMapping("/saveuser")
    public String saveUser(@RequestBody User user){
        ezbillingBo.saveUser(user);
        return "saved stock details";
    }
    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/getusers", method = RequestMethod.GET)
    public List<User> getUsers () throws ParseException {
        return ezbillingBo.getUsers();

    }
    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/getuser/{userName}", method = RequestMethod.GET)
    public User getUser (@PathVariable String userName){
        return ezbillingBo.getUser(userName);

    }
    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/setcnoascid", method = RequestMethod.GET)
    public String setCnoAsCid (){
       ezbillingBo.getCnoAsCid();
       return "success";

    }

    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/setpid", method = RequestMethod.GET)
    public String setPid (){
        ezbillingBo.setPid();
        return "success";

    }
    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/correctdate", method = RequestMethod.GET)
    public String correctDate (){
        ezbillingBo.changeDateFormat();
        return "success";

    }

    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/adddgst/{dgst}", method = RequestMethod.GET)
    public String addDgst (@PathVariable String dgst){
        ezbillingBo.addDgst(dgst);
        return "success";
    }

    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/dosetup/{dgst}", method = RequestMethod.GET)
    public String doSetup (@PathVariable String dgst){
        setCnoAsCid();
        setPid();
        correctDate();
        ezbillingBo.addDgst(dgst);
        ezbillingBo.correctAmount();
        copyProductsToStock(dgst);
        return "success";
    }
    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/correctamount", method = RequestMethod.GET)
    public String correctamount (@PathVariable String dgst){
        ezbillingBo.correctAmount();
        return "success";
    }

    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/getbillsamount/{dgst}", method = RequestMethod.GET)
    public List<BillAmountDetails> getBillsAmount (@PathVariable String dgst){
        return ezbillingBo.getBillsAmount(dgst);
    }
}
