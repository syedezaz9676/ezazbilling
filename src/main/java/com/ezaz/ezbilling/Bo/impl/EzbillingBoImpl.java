package com.ezaz.ezbilling.Bo.impl;

import com.ezaz.ezbilling.Bo.EzbillingBo;
import com.ezaz.ezbilling.model.Customer;
import com.ezaz.ezbilling.repository.CustomerRepository;
import com.ezaz.ezbilling.repository.CustomerRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class EzbillingBoImpl implements EzbillingBo {

    private CustomerRepository customerRepository;

    @Override
    public void saveCustomerToDB(Customer customer) {
//        Customer customerWithMaxId = customerRepository.maxCustomer();
//        customer.setCno(customerWithMaxId.getCno());
        System.out.println("customer"+customer.getCname());
        customerRepository.save(customer);
    }
}
