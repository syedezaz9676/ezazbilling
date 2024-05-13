package com.ezaz.ezbilling.repository;

import com.ezaz.ezbilling.model.BillAmountDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BillAmountRepository extends MongoRepository<BillAmountDetails,String> {
    List<BillAmountDetails>findAllByDgst(String dgst);


}
