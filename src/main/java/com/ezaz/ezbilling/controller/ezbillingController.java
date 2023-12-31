package com.ezaz.ezbilling.controller;

import com.ezaz.ezbilling.Bo.EzbillingBo;
import com.ezaz.ezbilling.model.*;
import com.ezaz.ezbilling.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//@CrossOrigin(origins = "http://localhost:8081/")
@RestController
public class ezbillingController {

    @Autowired
    private CompanyRepository companyRepository;

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
    @CrossOrigin(origins = "http://localhost:8081/")
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

    @RequestMapping(value="/getcustomernames/{id}", method = RequestMethod.GET)
    public List<CustomerNames> getCustomerNames(@PathVariable String id){
        return ezbillingBo.getCustomerNames(id);
    }

    @RequestMapping(value="/getcompanynames/{id}", method = RequestMethod.GET)
    public List<CompanyNames> getCompanyNames(@PathVariable String id){
        return ezbillingBo.getCompanyNames(id);
    }

    @RequestMapping(value="/getcustomerdetailsbyid/{id}", method = RequestMethod.GET)
    public ResponseEntity<Customer> getcutomerById(@PathVariable String id) {
        Optional<Customer> customer = ezbillingBo.getCustomerDetailsByID(id);
        return customer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @RequestMapping(value="/getcompanydetailsbyid/{id}", method = RequestMethod.GET)
    public ResponseEntity<CompanyDetails> getcompanyById(@PathVariable String id) {
        Optional<CompanyDetails> companydetails = ezbillingBo.getCompanyDetailsByID(id);
        return companydetails.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @RequestMapping(value="/getproductsbycompany/{companyname}", method = RequestMethod.GET)
    public List<ProductNames> getproductsByCompany(@PathVariable String companyname) {
        List<ProductNames> productsByCompany = ezbillingBo.getProductDetailsByCompany(companyname);
        return productsByCompany;
    }

    @RequestMapping(value="/getcustomerdetailsbydgst/{id}", method = RequestMethod.GET)
    public List<Customer> getcustomerDetaisByDgst(@PathVariable String id) {
        List<Customer> customerByDgst = ezbillingBo.getCustomerByDgst(id);
        return customerByDgst;
    }

    @PostMapping("/savebillingdetails")
    public ResponseEntity<?> savebillingdetails(@RequestBody List<BillingDetails> billDetails){
//        ezbillingBo.saveCustomerToDB(customer);
        System.out.println(billDetails.toString());

        String success= "sucess";
        return ResponseEntity.ok(success);
    }


}
