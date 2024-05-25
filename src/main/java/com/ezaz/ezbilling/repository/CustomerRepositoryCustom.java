package com.ezaz.ezbilling.repository;

import com.ezaz.ezbilling.model.Customer;
import com.ezaz.ezbilling.model.CustomerDetailswithGstNo;
import com.ezaz.ezbilling.model.CustomerNames;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepositoryCustom {
    Customer maxCustomer();

    public List<CustomerNames> getCustomerNames(String id);

    List<CustomerDetailswithGstNo> findGstCustomers();
    List<String> findGstCustomerIdsWithoutIgst();
    List<String> findGstCustomerIdsWithIgst();
    void addDgst(String dgst);
    public List<String> findGstCustomerIdsWithGst();
    public List<String> findCustomerIds();

}