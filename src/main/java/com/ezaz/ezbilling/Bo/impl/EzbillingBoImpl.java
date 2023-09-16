package com.ezaz.ezbilling.Bo.impl;

import com.ezaz.ezbilling.Bo.EzbillingBo;
import com.ezaz.ezbilling.model.Customer;
import com.ezaz.ezbilling.model.GstCodeDetails;
import com.ezaz.ezbilling.repository.CustomerRepository;
import com.ezaz.ezbilling.repository.GstCodeDetailsRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EzbillingBoImpl implements EzbillingBo {

    private final CustomerRepository customerRepository;
    private final GstCodeDetailsRepo gstCodeDetailsRepo;

    public EzbillingBoImpl(CustomerRepository customerRepository, GstCodeDetailsRepo gstCodeDetailsRepo) {
        this.customerRepository = customerRepository;
        this.gstCodeDetailsRepo = gstCodeDetailsRepo;
    }

    @Override
    public void saveCustomerToDB(Customer customer) {
        Customer customerWithMaxId = customerRepository.maxCustomer();
        customer.setCno(customerWithMaxId.getCno()+1);
        customerRepository.save(customer);
    }

    @Override
    public List<GstCodeDetails> getGstCodeDetails() {
        return gstCodeDetailsRepo.findAll();
    }
}