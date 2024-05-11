package com.ezaz.ezbilling.repository;

import com.ezaz.ezbilling.model.BillingDetails;
import com.ezaz.ezbilling.model.BillingItemDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChangeDateFormatRepo extends MongoRepository<BillingItemDetails, String> {
}
