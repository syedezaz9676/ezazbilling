package com.ezaz.ezbilling.controller;

import com.ezaz.ezbilling.Bo.EzbillingBo;
import com.ezaz.ezbilling.model.CompanyDetails;
import com.ezaz.ezbilling.model.Customer;
import com.ezaz.ezbilling.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ezbillingController {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private EzbillingBo ezbillingBo;

    @CrossOrigin(origins = "http://localhost:3000/")
    @PostMapping("/savecompanydetails")
    public void saveCompany(@RequestBody CompanyDetails company){
        companyRepository.save(company);
//        return "save success";
    }
    @GetMapping("/welcome")
    public String welcome(){
        return "welcome";
    }
    @PostMapping("/savecustomerdetails")
    public void saveCustomer(@RequestBody Customer customer){
        ezbillingBo.saveCustomerToDB(customer);
//        return "save success";
    }

}
