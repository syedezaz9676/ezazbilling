package com.ezaz.ezbilling.controller;

import com.ezaz.ezbilling.model.Company;
import com.ezaz.ezbilling.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ezbillingController {

    @Autowired
    private CompanyRepository companyRepository;

    @CrossOrigin(origins = "http://localhost:3000/")
    @PostMapping("/savecompanydetails")
    public void saveCompany(@RequestBody Company company){
        companyRepository.save(company);
//        return "save success";
    }
    @GetMapping("/welcome")
    public String welcome(){
        return "welcome";
    }

}
