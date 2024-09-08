package com.ezaz.ezbilling.repository;

import com.ezaz.ezbilling.model.BillAmountDetails;
import com.ezaz.ezbilling.model.UqcAndDescription;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UqcAndDescriptionRepository extends MongoRepository<UqcAndDescription,String> {
    UqcAndDescription findByHsnCode(String hsnCode);

}
