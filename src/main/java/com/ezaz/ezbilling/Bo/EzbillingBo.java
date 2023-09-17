package com.ezaz.ezbilling.Bo;

import com.ezaz.ezbilling.model.CompanyDetails;
import com.ezaz.ezbilling.model.Customer;
import com.ezaz.ezbilling.model.GstCodeDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;


public interface EzbillingBo {

    public void saveCustomerToDB(Customer customer);

    public List<GstCodeDetails> getGstCodeDetails();

    public void saveCompanyDetails(CompanyDetails companyDetails);

    public List<CompanyDetails> getCompanyDetails();
    }


