package com.ezaz.ezbilling.Bo;

import com.ezaz.ezbilling.model.Customer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;


public interface EzbillingBo {

    public void saveCustomerToDB(Customer customer);
    }
