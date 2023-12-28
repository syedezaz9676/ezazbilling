package com.ezaz.ezbilling.repository;

import com.ezaz.ezbilling.model.BillDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BillingRepositry  {

    String getMaxBillNo(String dgst);
}
