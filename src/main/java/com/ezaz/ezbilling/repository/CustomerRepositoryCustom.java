package com.ezaz.ezbilling.repository;

import com.ezaz.ezbilling.model.Customer;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepositoryCustom {
    Customer maxCustomer();
}