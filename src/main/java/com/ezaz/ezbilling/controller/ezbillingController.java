package com.ezaz.ezbilling.controller;

import com.ezaz.ezbilling.Bo.EzbillingBo;
import com.ezaz.ezbilling.model.CompanyDetails;
import com.ezaz.ezbilling.model.Customer;
import com.ezaz.ezbilling.model.GstCodeDetails;
import com.ezaz.ezbilling.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @RequestMapping(value="/getcompanydetails", method = RequestMethod.GET)
    public List<CompanyDetails> getCompanysDetails(){
        return ezbillingBo.getCompanyDetails();
    }


}
