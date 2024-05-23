package com.ezaz.ezbilling.repository;

import com.ezaz.ezbilling.model.BalanceDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BalanceDetailsRepository extends MongoRepository<BalanceDetails,String> {
    BalanceDetails findByCno(String cno);

    List<BalanceDetails> findByDgst(String dgst);
}
